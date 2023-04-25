package com.chessmaster.game.components.imageHandlersObjects;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.ByteBuffer;
import java.util.List;

final public class MLPModel {
//    private static Interpreter tflite;
//    private static TensorBuffer inputBuffer;
//    private static TensorBuffer outputBuffer;
//    private static int[] inputShape;
//    private static int[] outputShape;

    private MLPModel(String modelPath) throws IOException {
//        MappedByteBuffer modelFile = FileUtil.loadMappedFile(modelPath);
//        tflite = new Interpreter(modelFile);
//        inputShape = tflite.getInputTensor(0).shape();
//        outputShape = tflite.getOutputTensor(0).shape();
//        DataType inputDataType = tflite.getInputTensor(0).dataType();
//        DataType outputDataType = tflite.getOutputTensor(0).dataType();
//        inputBuffer = TensorBuffer.createFixedSize(inputShape, inputDataType);
//        outputBuffer = TensorBuffer.createFixedSize(outputShape, outputDataType);
    }

//    public static float[] predict(List<double[]> inputs) {
//        // Flatten the input to a 1D array
//        double[] flatInputs = flatten(inputs);
//
//        // Fill the input buffer
//        inputBuffer.loadArray(flatInputs);
//
//        // Run the model
//        tflite.run(inputBuffer.getBuffer(), outputBuffer.getBuffer());
//
//        // Get the output buffer
//        float[] flatOutputs = outputBuffer.getFloatArray();
//
//        // Reshape the output buffer to the original shape
//        float[][] outputs = reshape(flatOutputs, outputShape);
//
//        return outputs[0];
//    }
//
//    private static double[] flatten(List<double[]> inputs) {
//        int size = 1;
//        for (int k : inputShape) {
//            size *= k;
//        }
//        double[] flatInputs = new double[size];
//        int index = 0;
//        for (int i = 0; i < inputShape[0]; i++) {
//            for (int j = 0; j < inputShape[1]; j++) {
//                flatInputs[index++] = inputs.get(i)[j];
//            }
//        }
//        return flatInputs;
//    }
//
//    private static float[][] reshape(float[] flatOutputs, int[] shape) {
//        float[][] outputs = new float[shape[0]][shape[1]];
//        int index = 0;
//        for (int i = 0; i < shape[0]; i++) {
//            for (int j = 0; j < shape[1]; j++) {
//                outputs[i][j] = flatOutputs[index++];
//            }
//        }
//        return outputs;
//    }
}
