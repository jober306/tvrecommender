package util;

import static model.tensor.UserPreferenceTensorCollection.ANY;

import java.util.Comparator;

import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;

import model.tensor.UserPreferenceTensorCollection;

public class Comparators {
	
	
	public static Comparator<Integer> ChannelTensorComparator(UserPreferenceTensorCollection tensors) {
		return new Comparator<Integer>(){
			@Override
			public int compare(Integer channelId1, Integer channelId2) {
				int tensor1 = tensors.getUserPreferenceTensorsWatchTime(ANY, getChannelAsVector(channelId1), ANY);
				int tensor2 = tensors.getUserPreferenceTensorsWatchTime(ANY, getChannelAsVector(channelId2), ANY);
				return Integer.compare(tensor1, tensor2);
			}
			private Vector getChannelAsVector(int channelId) {
				return Vectors.dense(new double[] { channelId });
			}
		};
	}
}
