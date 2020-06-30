package outskirts.client.animation.loader.tmpmodelloader;

import outskirts.client.Loader;
import outskirts.client.animation.animated.AnimatedModel;
import outskirts.client.animation.animated.Joint;
import outskirts.client.animation.loader.tmpcolladaloader.MyFile;
import outskirts.client.animation.loader.tmpcolladaloader.colladaLoader.ColladaLoader;
import outskirts.client.animation.loader.tmpcolladaloader.dataStructures.AnimatedModelData;
import outskirts.client.animation.loader.tmpcolladaloader.dataStructures.JointData;
import outskirts.client.animation.loader.tmpcolladaloader.dataStructures.MeshData;
import outskirts.client.animation.loader.tmpcolladaloader.dataStructures.SkeletonData;
import outskirts.client.material.Model;

public class AnimatedModelLoader {

	public static AnimatedModel loadEntity(MyFile modelFile) {
		AnimatedModelData entityData = ColladaLoader.loadColladaModel(modelFile, 3);
		Model model = createVao(entityData.getMeshData());
		SkeletonData skeletonData = entityData.getJointsData();
		Joint headJoint = createJoints(skeletonData.headJoint);
		return new AnimatedModel(model, headJoint, skeletonData.jointCount);
	}

	private static Joint createJoints(JointData data) {
		Joint joint = new Joint(data.index, data.nameId, data.bindLocalTransform);
		for (JointData child : data.children) {
			joint.children.add(createJoints(child));
		}
		return joint;
	}

	private static Model createVao(MeshData data) {
	    float[] ids = new float[data.getJointIds().length];
	    for (int i = 0;i < ids.length;i++)
	        ids[i] = data.getJointIds()[i];
        return Loader.loadModel(data.getIndices(), 3,data.getVertices(), 2,data.getTextureCoords(), 3,data.getNormals(), 3,ids, 3,data.getVertexWeights());
	}

}
