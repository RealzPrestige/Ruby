package dev.zprestige.ruby.util.shader;

import net.minecraft.client.renderer.GlStateManager;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class Shader {

    private int program;
    private Map<String, Integer> uniformsMap;

    public Shader(String fragmentShader) {
        int vertexShaderID = 0, fragmentShaderID = 0;

        try {
            InputStream vertexStream = getClass().getResourceAsStream("/assets/minecraft/textures/ruby/shader/vertex.vert");
            if (vertexStream != null) {
                vertexShaderID = createShader(IOUtils.toString(vertexStream), ARBVertexShader.GL_VERTEX_SHADER_ARB);
            }
            IOUtils.closeQuietly(vertexStream);
            InputStream fragmentStream = getClass().getResourceAsStream("/assets/minecraft/textures/ruby/shader/frags/" + fragmentShader);
            if (fragmentStream != null) {
                fragmentShaderID = createShader(IOUtils.toString(fragmentStream), ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
            }
            IOUtils.closeQuietly(fragmentStream);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (vertexShaderID == 0 || fragmentShaderID == 0)
            return;

        program = ARBShaderObjects.glCreateProgramObjectARB();

        if (program == 0)
            return;

        ARBShaderObjects.glAttachObjectARB(program, vertexShaderID);
        ARBShaderObjects.glAttachObjectARB(program, fragmentShaderID);

        ARBShaderObjects.glLinkProgramARB(program);
        ARBShaderObjects.glValidateProgramARB(program);
    }

    public void startShader() {
        GlStateManager.pushMatrix();
        GL20.glUseProgram(program);

        if (uniformsMap == null) {
            uniformsMap = new HashMap<>();
            setupUniforms();
        }

        updateUniforms();
    }

    public void stopShader() {
        GL20.glUseProgram(0);
        GlStateManager.popMatrix();
    }

    public abstract void setupUniforms();

    public abstract void updateUniforms();

    private int createShader(String shaderSource, int shaderType) {
        int shader = 0;

        try {
            shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);

            if (shader == 0)
                return 0;

            ARBShaderObjects.glShaderSourceARB(shader, shaderSource);
            ARBShaderObjects.glCompileShaderARB(shader);

            if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE)
                throw new RuntimeException("Error creating shaders: " + getLogInfo(shader));

            return shader;
        } catch (Exception e) {
            ARBShaderObjects.glDeleteObjectARB(shader);
            throw e;

        }
    }

    private String getLogInfo(int i) {
        return ARBShaderObjects.glGetInfoLogARB(i, ARBShaderObjects.glGetObjectParameteriARB(i, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
    }

    public void setUniform(String uniformName, int location) {
        uniformsMap.put(uniformName, location);
    }

    public void setupUniform(String uniformName) {
        setUniform(uniformName, GL20.glGetUniformLocation(program, uniformName));
    }

    public int getUniform(String uniformName) {
        return uniformsMap.get(uniformName);
    }
}
