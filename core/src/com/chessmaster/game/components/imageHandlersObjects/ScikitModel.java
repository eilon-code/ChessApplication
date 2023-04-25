//package com.chessmaster.game.components.imageHandlersObjects;
//
//import android.annotation.TargetApi;
//import android.os.Build;
//
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//import net.razorvine.pyro.PyroProxy;
//
//public class ScikitModel {
//    private final PyroProxy model;
//
//    @TargetApi(Build.VERSION_CODES.O)
//    public ScikitModel(String modelPath) throws IOException, ClassNotFoundException {
//        // Load the model from the serialized file
//        ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(Paths.get(modelPath)));
//        this.model = (PyroProxy) ois.readObject();
//    }
//
//    public void predict(){
//
//
//        // Make some example predictions
//        List<List<Double>> samples = new ArrayList<>();
//        samples.add(List.of(1.0, 2.0, 3.0));
//        samples.add(List.of(4.0, 5.0, 6.0));
//        Object[] predictions = (Object[]) model.call("predict", samples.toArray());
//
//        // Print the predictions
//        for (Object prediction : predictions) {
//            System.out.println(prediction);
//        }
//    }
//    public List<String> predict(List<double[]> X) {
//        List<String> labels = new ArrayList<>();
//        List<Row> data = new ArrayList<>();
//
//        for (double[] input : X) {
//            Vector vector = Vectors.dense(input);
//            data.add(RowFactory.create(vector));
//        }
//
//        StructType schema = new StructType(new StructField[] {
//                DataTypes.createStructField("features", SQLDataTypes.VectorType(), true)
//        });
//
//        SparkSession spark = SparkSession.builder().getOrCreate();
//        Dataset<Row> dataFrame = spark.createDataFrame(data, schema);
//
//        Dataset<Row> output = this.model.transform(dataFrame);
//
//        for (Row row : output.collectAsList()) {
//            Vector vector = (Vector) row.get(row.fieldIndex("prediction"));
//            double[] array = vector.toArray();
//            String label = String.valueOf(array[0]);
//            labels.add(label);
//        }
//
//        return labels;
//    }
//}
