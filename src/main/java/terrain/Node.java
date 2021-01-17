package terrain;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private Node parent;
    public List<Node> children;

    public Vector3f worldTranslation = new Vector3f(0, 0, 0);
    public Vector3f worldScaling = new Vector3f(1, 1, 1);

    public Matrix4f getWorldMatrix() {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(worldTranslation);
        matrix.scale(worldScaling);
        return matrix;
    }

    public Vector3f localTranslation = new Vector3f(0, 0, 0);
    public Vector3f localScaling = new Vector3f(1, 1, 1);

    public Matrix4f getLocalMatrix() {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(localTranslation);
        matrix.scale(localScaling);
        return matrix;
    }

    public Node() {
        setChildren(new ArrayList<>());
    }

    public void addChild(Node child) {
        child.setParent(this);
        children.add(child);
    }

    public void  render(TerrainShader shader) {
        for (Node child : children)
            child.render(shader);
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

}

