package com.mkts.MiniGLModel.shapes;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.mkts.MiniGLModel.R;
import com.mkts.MiniGLModel.glRenderer;
import com.mkts.MiniGLModel.utils.PartEntry;
import com.mkts.MiniGLModel.utils.miniGeometry;

public class Mini {
	Context context;
	
	public static final int FLOAT_SIZE = 4;
	public static final int SHORT_SIZE = 2;
	
	miniGeometry miniModel;
	
	private int numParts;	
	private float color[] = new float[4];
	private ArrayList<PartEntry> groups;

	private final FloatBuffer mVertexBuffer;
	private final FloatBuffer mNormalBuffer;
	private final ShortBuffer mIndexBuffer;	
	private final FloatBuffer mTexCoordsBuffer;
	private final int[] ibo = new int[1];
	
	private final int mProgram;
	private int mPositionHandle;
	private int mColorHandle;
	private int mMVMatHandle;
	private int mMVPMatHandle;	
	private int mTextCoordsHandle;
	private int mUseTextureHandle;
	private int mNormalHandle;

	// Vertex Shader
	private final String vertexShaderCode = 
					"uniform mat4 uMVPMat;" +
					"uniform mat4 uMVMat;" +
					"attribute vec4 a_Position;" +
					"attribute vec2 a_texCoords;" +
					"attribute vec3 a_normal;" +
					"varying vec3 v_Position;" +
					"varying vec2 v_texCoords;" +
					"varying vec3 v_normal;" +
					"void main() {" +
					"  v_Position = vec3(uMVMat * a_Position);" +
					"  v_texCoords = a_texCoords;" +
					"  v_normal = vec3(uMVMat * vec4(a_normal, 0.0));" +
					"  gl_Position = uMVPMat * a_Position;" +
					"}";

	// Fragment shader
	private final String fragmentShaderCode = 
					"precision mediump float;" +
					"uniform vec4 uColor;" +
					"uniform bool useTexture;" +
					"uniform sampler2D miniTexture;" +
					"varying vec3 v_Position;" +
					"varying vec2 v_texCoords;" +
					"varying vec3 v_normal;" +
					"vec3 lightPos = vec3(50.0, 50.0, -200.0);" +
					"vec3 specColor = vec3(1.0, 1.0, 1.0);" +
					"vec4 diffuseColor;" +
					"vec4 ambientColor;" +
					"const float Ia = 0.2;" +
					"const float Id = 0.85;" +
					"const float Is = 0.30;" +
					"void main() {" +
					"  if(useTexture)" +
					"  {" +
					"    diffuseColor = texture2D(miniTexture, v_texCoords);" +
					"    ambientColor = texture2D(miniTexture, v_texCoords);" +
					"  }" +
					"  else" +
					"  {" +
					"    diffuseColor = uColor;" +
					"    ambientColor = uColor;" +
					"  }" +
					"  vec3 lightVector = normalize(lightPos - v_Position);" +
					"  float diffuse = max(dot(v_normal, lightVector), 0.0);" +
					"  vec3 normal = normalize(v_normal);" +
					"  vec3 reflectVector = reflect(-lightVector, normal);" +
					"  vec3 viewVector = normalize(-v_Position);" +
					"  float specular = 0.0;" +
					"  if(diffuse > 0.0)" +
					"  {" +
					"    float specAngle = max(dot(lightVector, normal), 0.0);" +
					"    specular = pow(specAngle, 1.0);" +
					"  }" +
					"  gl_FragColor = ambientColor * Ia + diffuse * diffuseColor * Id + specular * specColor * Is;" +
					"}";

	// Array for each mini part and its color
	public ColorEntry colorMap[] = {
			new ColorEntry("Upper Driver Wiper", 0.2f, 0.2f, 0.2f),
			new ColorEntry("Upper Passenger Wiper", 0.2f, 0.2f, 0.2f),
			new ColorEntry("Lower Driver Wiper", 0.2f, 0.2f, 0.2f),
			new ColorEntry("Lower Passenger Wiper", 0.2f, 0.2f, 0.2f),
			new ColorEntry("Rear Wiper", 0.2f, 0.2f, 0.2f),
			new ColorEntry("Vents", 0.1f, 0.1f, 0.1f),
			new ColorEntry("License", 0.94f, 0.64f, 0.19f),
			new ColorEntry("Front Driver Rim", 0.75f, 0.75f, 0.75f),
			new ColorEntry("Front Passenger Rim", 0.75f, 0.75f, 0.75f),
			new ColorEntry("Rear Driver Rim", 0.75f, 0.75f, 0.75f),
			new ColorEntry("Rear Passenger Rim", 0.75f, 0.75f, 0.75f),
			new ColorEntry("Front Driver Tire", 0.1f, 0.1f, 0.1f),
			new ColorEntry("Front Passenger Tire", 0.1f, 0.1f, 0.1f),
			new ColorEntry("Rear Driver Tire", 0.1f, 0.1f, 0.1f),
			new ColorEntry("Rear Passenger Tire", 0.1f, 0.1f, 0.1f),
			new ColorEntry("Brakes", 0.75f, 0.75f, 0.75f),
	};

	public Mini(Context context) throws FileNotFoundException {
		this.context = context;

		// Initialize the mini model and data arrays
		miniModel = new miniGeometry(context);
		float vertices[] = miniModel.getVertexData();		
		short indices[] = miniModel.getIndices();
		float texCoords[] = miniModel.getTexData();
		float normals[] = miniModel.getNormalData();
		
		// Method call to load in the texture
		loadTexture(context);
		
		// Initialize buffers
		mVertexBuffer = 
				ByteBuffer.allocateDirect(vertices.length * FLOAT_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mVertexBuffer.put(vertices).position(0);
		
		mTexCoordsBuffer = 
				ByteBuffer.allocateDirect(texCoords.length * FLOAT_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mTexCoordsBuffer.put(texCoords).position(0);
		
		mNormalBuffer =
				ByteBuffer.allocateDirect(normals.length * FLOAT_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mNormalBuffer.put(normals).position(0);

		mIndexBuffer = 
				ByteBuffer.allocateDirect(indices.length * SHORT_SIZE).order(ByteOrder.nativeOrder()).asShortBuffer();
		mIndexBuffer.put(indices).position(0);
		
		// Create and bind the index buffer with OpenGL
		GLES20.glGenBuffers(1, ibo, 0);		
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, mIndexBuffer.capacity()
				* SHORT_SIZE, mIndexBuffer, GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

		// Load the shaders
		int vertexShader = glRenderer.loadShader(
				GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int fragmentShader = glRenderer.loadShader(
				GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

		// Bind the shaders with OpenGL
		mProgram = GLES20.glCreateProgram();
		GLES20.glAttachShader(mProgram, vertexShader);
		GLES20.glAttachShader(mProgram, fragmentShader);
		GLES20.glLinkProgram(mProgram);
	}

	public void draw(float[] mvMat, float[] mvpMat) {
		GLES20.glUseProgram(mProgram);
		
		// Handles position attribute
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(
				mPositionHandle, 3, GLES20.GL_FLOAT, false,
				0, mVertexBuffer);
		
		// Handles texture coordinates attribute
		mTextCoordsHandle = GLES20.glGetAttribLocation(mProgram, "a_texCoords");
		GLES20.glEnableVertexAttribArray(mTextCoordsHandle);
		GLES20.glVertexAttribPointer(
				mTextCoordsHandle, 2, GLES20.GL_FLOAT, false,
				0, mTexCoordsBuffer);
		
		// Handles normal coordinates attribute
		mNormalHandle = GLES20.glGetAttribLocation(mProgram, "a_normal");
		GLES20.glEnableVertexAttribArray(mNormalHandle);
		GLES20.glVertexAttribPointer(
				mNormalHandle, 3, GLES20.GL_FLOAT, false,
				0, mNormalBuffer);
		
		// Handles the 'use texture' uniform
		mUseTextureHandle = GLES20.glGetUniformLocation(mProgram, "useTexture");
		GLES20.glUniform1i(mUseTextureHandle, 1);
		
		// Handles the MV uniform
		mMVMatHandle = GLES20.glGetUniformLocation(mProgram, "uMVMat");
		GLES20.glUniformMatrix4fv(mMVMatHandle, 1, false, mvMat, 0);
		
		// Handles the MVP uniform
		mMVPMatHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMat");
		GLES20.glUniformMatrix4fv(mMVPMatHandle, 1, false, mvpMat, 0);
		
		// Handle the color uniform
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "uColor");
		
		// Set the colors and draw each part of the mini
		groups = new ArrayList<PartEntry>(miniModel.getGroups());
		numParts = miniModel.getNumParts();
		PartEntry entry = new PartEntry();
		for(int i = 0; i < numParts; i++) {
			entry = groups.get(i);	
			String name = entry.name;
			
			// Decide whether to use the texture or not
			if(name.equals("Windows") || name.contains("Wiper") ||
			   name.equals("Vents") || name.equals("License") || name.equals("Brakes") ||
			   name.equals("Rear View Mirror") || name.equals("Interior") ||
			   name.equals("Driver") || name.equals("Chair") || name.contains("Rim")|| 
			   name.contains("Tire")) 
			{
				GLES20.glUniform1i(mUseTextureHandle, 0);
			} else {
				GLES20.glUniform1i(mUseTextureHandle, 1);
			}
			
			int offset = 2 * miniModel.getIndicesPerFace() * entry.start;
			int count = miniModel.getIndicesPerFace() * (entry.end - entry.start);

			for(int k = 0; k < colorMap.length; k++) {
				if(name.equals(colorMap[k].part)) {
					color[0] = colorMap[k].color[0];
					color[1] = colorMap[k].color[1];	
					color[2] = colorMap[k].color[2];
					color[3] = 1.0f;
				}
			}
			
			// Links the buffers and draws the parts
			GLES20.glUniform4fv(mColorHandle, 1, color, 0);
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
			GLES20.glDrawElements(GLES20.GL_TRIANGLES, count, GLES20.GL_UNSIGNED_SHORT, offset);
		}

		// Unlink the attribute buffers
		GLES20.glDisableVertexAttribArray(mPositionHandle);
		GLES20.glDisableVertexAttribArray(mTextCoordsHandle);
		GLES20.glDisableVertexAttribArray(mNormalHandle);
	}
	
	public static void loadTexture(Context context) {
		// Get bitmap
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.raw.mini_texture);
		
		// Flip the image for OpenGL
		// java starts in the top, left corner
		// OpenGL starts in the bottom, left corner
		bitmap = flip(bitmap);
		
		// Get new buffer ID
		int textures[] = new int[1];
		GLES20.glGenBuffers(1, textures, 0);

		// Bind texture to OpenGL
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);		
		
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);	
		
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
		
		bitmap.recycle();
	}
	
	// Flip a bitmap vertically
	public static Bitmap flip(Bitmap src) {
		Matrix matrix = new Matrix();
		matrix.preScale(1.0f, -1.0f);
		
		return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
	}
}

// Class for mapping each part with its color
class ColorEntry {
	String part;
	float color[]= new float[3];
	
	public ColorEntry(String part, float color0, float color1, float color2) {
		this.part = part;
		this.color[0] = color0;
		this.color[1] = color1;
		this.color[2] = color2;
	}
}