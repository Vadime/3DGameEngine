package terrain;


import model.Texture;

public class TerrainMaterial {

    public Texture diffuseMap, normalMap, displaceMap;
    float displaceScale, horizontalScale;

    public Texture getDiffuseMap() {
        return diffuseMap;
    }

    public void setDiffuseMap(Texture diffuseMap) {
        this.diffuseMap = diffuseMap;
    }

    public Texture getNormalMap() {
        return normalMap;
    }

    public void setNormalMap(Texture normalMap) {
        this.normalMap = normalMap;
    }

    public Texture getDisplaceMap() {
        return displaceMap;
    }

    public void setDisplaceMap(Texture displaceMap) {
        this.displaceMap = displaceMap;
    }

    public float getDisplaceScale() {
        return displaceScale;
    }

    public void setDisplaceScale(float displaceScale) {
        this.displaceScale = displaceScale;
    }

    public float getHorizontalScale() {
        return horizontalScale;
    }

    public void setHorizontalScale(float horizontalScale) {
        this.horizontalScale = horizontalScale;
    }
}
