package terrain;

import gpgpu.NormalMapRenderer;
import gpgpu.SplatMapRenderer;
import model.Texture;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import util.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class TerrainConfig {

    public static float scaleY;
    public static float scaleXZ;
    public static Texture heightMap;
    public static Texture normalMap;
    public static Texture splatMap;
    public static FloatBuffer heightMapDataBuffer;
    public static int tessellationFactor;
    public static float tessellationSlope;
    public static float tessellationShift;
    public static int tbn_Range;
    public static final int[] lod_range = new int[8];
    public static final int[] lod_morphing_area = new int[8];

    public static List<TerrainMaterial> materials = new ArrayList<>();

    public static void init(String file) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] tokens = Utils.removeEmptyStrings(line.split(" "));
            if (tokens.length == 0) continue;
            if (tokens[0].equals("scaleY")) scaleY = (Float.parseFloat(tokens[1]));
            if (tokens[0].equals("scaleXZ")) scaleXZ = (Float.parseFloat(tokens[1]));
            if (tokens[0].equals("tessellationFactor")) tessellationFactor = (Integer.parseInt(tokens[1]));
            if (tokens[0].equals("tessellationSlope")) tessellationSlope = (Float.parseFloat(tokens[1]));
            if (tokens[0].equals("tessellationShift")) tessellationShift = (Float.parseFloat(tokens[1]));
            if (tokens[0].equals("#lod_ranges")) {
                for (int i = 0; i < 8; i++) {
                    line = reader.readLine();
                    tokens = line.split(" ");
                    tokens = Utils.removeEmptyStrings(tokens);
                    if (tokens[0].equals("lod" + (i + 1) + "_range")) {
                        if (Integer.parseInt(tokens[1]) == 0) {
                            lod_range[i] = 0;
                            lod_morphing_area[i] = 0;
                        } else {
                            int lod_range = Integer.parseInt(tokens[1]);
                            TerrainConfig.lod_range[i] = lod_range;
                            int updateMorphingArea = (int) ((scaleXZ / Terrain.rootNodes) / (Math.pow(2, i + 1)));
                            lod_morphing_area[i] = lod_range - updateMorphingArea;
                        }
                    }
                }
            }
            if (tokens[0].equals("heightMap")) {
                heightMap = (new Texture("res/settings/" + tokens[1]));
                heightMap.bind();
                heightMap.bilinearFilter();
                heightMapDataBuffer = BufferUtils.createFloatBuffer(heightMap.getWidth() * heightMap.getHeight());
                heightMap.bind();
                GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RED, GL11.GL_FLOAT, heightMapDataBuffer);
                NormalMapRenderer normalRenderer = new NormalMapRenderer(heightMap.getWidth());
                normalRenderer.setStrength(60);
                normalRenderer.render(heightMap);
                normalMap = (normalRenderer.getNormalmap());
                SplatMapRenderer splatmapRenderer = new SplatMapRenderer(heightMap.getWidth());
                splatmapRenderer.render(normalMap);
                splatMap = (splatmapRenderer.getSplatmap());
            }
            if (tokens[0].equals("material")) materials.add(new TerrainMaterial());
            if (tokens[0].equals("material_DIF")) {
                Texture diffusemap = new Texture(tokens[1]);
                diffusemap.bind();
                diffusemap.trilinearFilter();
                materials.get(materials.size() - 1).setDiffuseMap(diffusemap);
            }
            if (tokens[0].equals("material_NRM")) {
                Texture normalmap = new Texture(tokens[1]);
                normalmap.bind();
                normalmap.trilinearFilter();
                materials.get(materials.size() - 1).setNormalMap(normalmap);
            }
            if (tokens[0].equals("material_DISP")) {
                Texture heightmap = new Texture(tokens[1]);
                heightmap.bind();
                heightmap.trilinearFilter();
                materials.get(materials.size() - 1).setDisplaceMap(heightmap);
            }
            if (tokens[0].equals("material_heightScaling"))
                materials.get(materials.size() - 1).setDisplaceScale(Float.parseFloat(tokens[1]));
            if (tokens[0].equals("material_horizontalScaling"))
                materials.get(materials.size() - 1).setHorizontalScale(Float.parseFloat(tokens[1]));
            if (tokens[0].equals("tbn_Range")) tbn_Range = (Integer.parseInt(tokens[1]));
        }
        reader.close();
    }

}
