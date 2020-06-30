package outskirts.client.animation.loader.tmpmodelloader;

import outskirts.client.animation.Animation;
import outskirts.client.animation.loader.tmpcolladaloader.MyFile;
import outskirts.client.animation.loader.tmpcolladaloader.colladaLoader.ColladaLoader;
import outskirts.client.animation.loader.tmpcolladaloader.dataStructures.AnimationData;
import outskirts.client.animation.loader.tmpcolladaloader.dataStructures.JointTransformData;
import outskirts.client.animation.loader.tmpcolladaloader.dataStructures.KeyFrameData;
import outskirts.util.vector.Matrix4f;
import outskirts.util.vector.Quaternion;
import outskirts.util.vector.Vector3f;

import java.util.HashMap;
import java.util.Map;

/**
 * This class loads up an animation collada file, gets the information from it,
 * and then creates and returns an {@link Animation} from the extracted data.
 * 
 * @author Karl
 *
 */
public class AnimationLoader {
//
//	/**
//	 * Loads up a collada animation file, and returns and animation created from
//	 * the extracted animation data from the file.
//	 *
//	 * @param colladaFile
//	 *            - the collada file containing data about the desired
//	 *            animation.
//	 * @return The animation made from the data in the file.
//	 */
	public static Animation loadAnimation(MyFile colladaFile) {
		AnimationData animationData = ColladaLoader.loadColladaAnimation(colladaFile);
		Animation.KeyFrame[] frames = new Animation.KeyFrame[animationData.keyFrames.length];
		for (int i = 0; i < frames.length; i++) {
			frames[i] = createKeyFrame(animationData.keyFrames[i]);
		}
		return new Animation(animationData.lengthSeconds, frames);
	}
//
//	/**
//	 * Creates a keyframe from the data extracted from the collada file.
//	 *
//	 * @param data
//	 *            - the data about the keyframe that was extracted from the
//	 *            collada file.
//	 * @return The keyframe.
//	 */
	private static Animation.KeyFrame createKeyFrame(KeyFrameData data) {
		Map<String, Animation.KeyFrame.JointTransform> map = new HashMap<String, Animation.KeyFrame.JointTransform>();
		for (JointTransformData jointData : data.jointTransforms) {
			Animation.KeyFrame.JointTransform jointTransform = createTransform(jointData);
			map.put(jointData.jointNameId, jointTransform);
		}
		return new Animation.KeyFrame(data.time, map);
	}
//
//	/**
//	 * Creates a joint transform from the data extracted from the collada file.
//	 *
//	 * @param data
//	 *            - the data from the collada file.
//	 * @return The joint transform.
//	 */
	private static Animation.KeyFrame.JointTransform createTransform(JointTransformData data) {
		Matrix4f mat = data.jointLocalTransform;
		Vector3f translation = new Vector3f(mat.m03, mat.m13, mat.m23);
		Quaternion rotation = Quaternion.fromMatrix(mat, null);
		return new Animation.KeyFrame.JointTransform(translation, rotation);
	}

}
