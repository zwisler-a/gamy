package engine.renderer;

import common.Maths;
import engine.Game;
import engine.entity.Entity;
import engine.model.Model;
import engine.model.TexturedModel;
import engine.scene.Scene;
import engine.shader.shadowShader.ShadowShader;
import org.joml.Matrix4f;
import org.lwjgl.opengl.*;

import java.util.LinkedList;

public class ShadowRenderer {
    private ShadowShader shader;

    public ShadowRenderer() {
        this.shader = new ShadowShader();
        shader.start();
        shader.loadProjectionMatrix(Renderer.createProjectionMatrix(Game.gameSettings.resolutionX,Game.gameSettings.resolutionY));
        shader.stop();
    }

    public void render(Scene s, Matrix4f viewMatrix) {

        shader.start();
        shader.loadViewMatrix(viewMatrix);
        for (TexturedModel model : s.getEntities(StaticRenderer.class).keySet()) {
            prepareTexturedModel(model);
            LinkedList<Entity> singleE = s.getEntities(StaticRenderer.class).get(model);
            for (Entity entity : singleE) {
                prepareEntity(entity);
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getModel().getIndiciesCount(), GL11.GL_UNSIGNED_INT, 0);
            }
        }
        unbindTexturedModel();
        shader.stop();
    }

    private void prepareTexturedModel(TexturedModel model) {
        Model rawModel = model.getModel();
        GL30.glBindVertexArray(rawModel.getVaoId());
        GL20.glEnableVertexAttribArray(0);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, rawModel.getIndicesVBO());
    }

    private void prepareEntity(Entity entity) {
        Matrix4f transMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotation().x, entity.getRotation().y, entity.getRotation().z, entity.getScale());
        shader.loadTransformationMatrix(transMatrix);
    }

    private void unbindTexturedModel() {
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }

    public void cleanUp() {
        shader.cleanUp();
    }


}
