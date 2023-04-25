package com.chessmaster.game.components.imageHandlersObjects;

import com.chessmaster.game.components.gameObjects.BoardObjects.Board;
import com.chessmaster.game.components.gameObjects.BoardObjects.Cell;
import com.chessmaster.game.components.gameObjects.GamePieces.Color;
import com.chessmaster.game.components.gameObjects.GamePieces.PieceType;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class ImageProcessor {
//    private static final ScikitModel svmCell = new ScikitModel("path/to/model");
//    private static final ScikitModel svmOccupancy = new ScikitModel("path/to/model");
//    private static final ScikitModel svmPiece = new ScikitModel("path/to/model");
//    private static final ScikitModel knnCell = new ScikitModel("path/to/model");
//    private static final ScikitModel knnOccupancy = new ScikitModel("path/to/model");
//    private static final ScikitModel knnPiece = new ScikitModel("path/to/model");
//    private static final ScikitModel mlpCell = new ScikitModel("core/classification_models/Cell Classification");
//    private static final ScikitModel mlpOccupancy = new ScikitModel("path/to/model");
//    private static final ScikitModel mlpPiece = new ScikitModel("path/to/model");

    public static Board detectBord(String imageFilePath){
        File imageFile = new File(imageFilePath);
        if ((!imageFile.exists()) || (!imageFile.canRead())) {
            System.out.println("Can't Fucking read");
            return new Board(null, null, Color.White);
        }

//        BufferedImage bufferedImage = ImageIO.read(imageFile);
//        byte[] data = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
//        Mat image = new Mat(bufferedImage.getHeight(),bufferedImage.getWidth(), CvType.CV_8UC3);

//        //Loading the core library
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        //Instantiating the Imgcodecs class
//        Imgcodecs imageCodecs = new Imgcodecs();

        Mat image = Imgcodecs.imread(imageFilePath);

        Mat transformedImage = getTransformedImage(image);
        List<Mat> cells = getCellsImages(transformedImage);

        List<double[]> X = prepareForEvaluation(cells);
        List<String> labels = new ArrayList<>();// mlpCell.predict(X);

        return createBoardFromLabels(labels);
    }

    private static Board createBoardFromLabels(List<String> labels){
        Board resultBoard = new Board(null, null, Color.White);
        int i = 0;
        for (String  label : labels) {
            int row = i / 8;
            int column = i % 8;
            boolean isTherePiece = label.equals("empty");
            Color color = Color.None;
            PieceType pieceType = PieceType.None;
            if (!isTherePiece){
                String[] cellValues = label.split("-");
                color = (cellValues[0].equals("white") ? Color.White : Color.Black);
                pieceType = PieceType.getPieceType(cellValues[1]);
            }
            resultBoard.cellsGrid[i / 8][i % 8] = new Cell(row, column, isTherePiece, color, pieceType);
            i++;
        }
        return resultBoard;
    }

    public static Mat normalizeFunc(Mat x) {
        Mat normalized = new Mat();
        Core.divide(x, new Scalar(255.0), normalized);
        return normalized;
    }

    public static List<double[]> preprocess(List<Mat> images) {
        List<double[]> X = new ArrayList<>();
        for (Mat image : images) {
            image.convertTo(image, CvType.CV_32F);
            image = normalizeFunc(image);
            image.convertTo(image, CvType.CV_64F);

            double[] x = new double[(int) image.total() * image.channels()];
            image.get(0, 0, x);
            X.add(x);
        }
        return X;
    }

    public static List<double[]> prepareForEvaluation(List<Mat> images) {
        List<Mat> reshapedImages = new ArrayList<>();
        for (Mat cellImage : images){
            reshapedImages.add(cellImage.reshape(1, 1));
        }
        return preprocess(reshapedImages);
    }

    /**
     * Detect edges in an image using Canny edge detection.
     * @param image the input image
     * @param threshold1 the lower threshold for the hysteresis procedure
     * @param threshold2 the upper threshold for the hysteresis procedure
     * @return the binary edge image
     */
    public static Mat getEdges(Mat image, double threshold1, double threshold2) {
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        Mat edges = new Mat();
        Imgproc.Canny(gray, edges, threshold1, threshold2);
        return edges;
    }

    /**
     * Apply a perspective transformation to an image to rectify its perspective.
     * @param image the input image
     * @return the transformed image
     */
    public static Mat getTransformedImage(Mat image) {
        Mat result = new Mat();

        Imgproc.resize(image, image, new Size(500, 500));
        Mat edges = ImageProcessor.getEdges(image, 130, 250);

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(edges, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        MatOfPoint biggestContour = null;
        double maxArea = 0;
        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            if (area > maxArea) {
                biggestContour = contour;
                maxArea = area;
            }
        }
        if (biggestContour == null){
            return image;
        }
        MatOfInt hull = new MatOfInt();
        Imgproc.convexHull(biggestContour, hull);
        MatOfPoint hullPoints = new MatOfPoint(hull);

        double epsilon = 0.05 * Imgproc.arcLength(new MatOfPoint2f(hullPoints.toArray()), true);
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        Imgproc.approxPolyDP(new MatOfPoint2f(hullPoints.toArray()), approxCurve, epsilon, true);
        Point[] srcPoints = new Point[4];
        srcPoints[0] = new Point(image.cols() * 0.95, image.rows() * 0.05);
        srcPoints[1] = new Point(image.cols() * 0.95, image.rows() * 0.95);
        srcPoints[2] = new Point(image.cols() * 0.05, image.rows() * 0.95);
        srcPoints[3] = new Point(image.cols() * 0.05, image.rows() * 0.05);
        MatOfPoint2f dstPoints = new MatOfPoint2f(srcPoints);

        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(approxCurve, dstPoints);
        Imgproc.warpPerspective(image, result, perspectiveTransform, image.size());

        return result;
    }

    public static List<Mat> getCellsImages(Mat transformedImage) {
        Mat edges = ImageProcessor.getEdges(transformedImage, 100, 180);
        Mat lines = new Mat();
        Imgproc.HoughLines(edges, lines, 1, Math.PI / 180, 120);
        lines = ImageProcessor.filterHoughLines(lines, 1, 30);

        List<int[][]> cartesianLines = convertLinesToCartesian(lines, transformedImage.size());
        cartesianLines = filterCartesianLines(cartesianLines, 1, 30);

        Map<String, List<int[][]>> lineGroups = splitLinesAndRemoveOutliers(cartesianLines);
        List<int[]> intersections = getIntersections(lineGroups);
        intersections = getAllIntersections(filterIntersections(intersections));
        sortIntersections(intersections);

        List<int[]> cellsCenters = getCellsCenters(intersections);

        return get64Subimages(transformedImage, cellsCenters);
    }

    public static List<Mat> get64Subimages(Mat img, List<int[]> middleCells) {
        List<Mat> images = new ArrayList<>();

        for (int[] point : middleCells) {
            double width = img.size().height / 8;
            double height = img.size().width / 6;
            int xCenter = point[0];
            int yCenter = point[1];

            int startX = Math.max((int) (xCenter - (width / 2)), 0);
            int endX = (int) Math.min((int) (xCenter + (width / 2)), img.size().height);
            int startY = Math.max((int) (yCenter - (height / 2)), 0);
            int endY = (int) Math.min((int) (yCenter + (height / 2)), img.size().width);

            Mat currentImg = new Mat(img, new Rect(new Point(startX, startY), new Point(endX, endY)));
            Imgproc.resize(currentImg, currentImg, new Size(64, 64));
            images.add(currentImg);
        }

        return images;
    }

    public static List<int[]> getCellsCenters(List<int[]> intersections) {
        List<int[]> points = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (i + 1 + (j + 1) * 9 >= intersections.size()) {
                    continue;
                }
                int sumX = 0;
                int sumY = 0;
                for (int i_point = i; i_point <= i+1; i_point++){
                    for (int j_point = j; j_point <= j+1; j_point++){
                        int[] point = intersections.get(i_point + j_point * 9);
                        sumX += point[0];
                        sumY += point[1];
                    }
                }
                points.add(new int[] {sumX / 4, sumY / 4});
            }
        }
        return points;
    }

    public static void sortIntersections(List<int[]> intersections) {
        // Sort the points by their x values
//        intersections.sort(Comparator.comparingInt(point -> point[0]));
        Collections.sort(intersections, new Comparator<int[]>() {
            public int compare(int[] point1, int[] point2) {
                return Integer.compare(point1[0], point2[0]);
            }
        });

        // Split the list into groups of 9 elements
        List<List<int[]>> groups = new ArrayList<>();
        for (int i = 0; i < intersections.size(); i += 9) {
            groups.add(intersections.subList(i, Math.min(i + 9, intersections.size())));
        }

        // Sort each group by their y values
        for (List<int[]> group : groups) {
//            group.sort(Comparator.comparingInt(point -> point[1]));
            Collections.sort(group, new Comparator<int[]>() {
                public int compare(int[] point1, int[] point2) {
                    return Integer.compare(point1[1], point2[1]);
                }
            });
        }
    }

    public static List<int[]> filterIntersections(List<int[]> intersections) {
        int maxDistToCount = 40;
        List<List<int[]>> closeIds = new ArrayList<>();
        List<Integer> remainIds = new ArrayList<>();
        for (int i = 0; i < intersections.size(); i++) {
            remainIds.add(i);
            // rest of the loop code
        }
        for (int i = 0; i < intersections.size(); i++) {
            if (!remainIds.contains(i)) {
                continue;
            }
            closeIds.add(new ArrayList<int[]>());
            for (int j = 0; j < intersections.size(); j++) {
                int[] p1 = intersections.get(i);
                int[] p2 = intersections.get(j);
                double distance = Math.sqrt(Math.pow((p1[0]-p2[0]), 2) + Math.pow((p1[1]-p2[1]), 2));
                if (distance <= maxDistToCount) {
                    closeIds.get(closeIds.size()-1).add(intersections.get(j));
                    if (remainIds.contains(j) && i != j) {
                        remainIds.remove((Integer) j);
                    }
                }
            }
        }
        List<int[]> filteredIntersections = new ArrayList<>();
        for (List<int[]> group : closeIds) {
            if (group.size() <= 1) {
                continue;
            }
            int avgX = 0, avgY = 0;
            for (int[] val : group) {
                avgX += val[0];
                avgY += val[1];
            }
            avgX /= group.size();
            avgY /= group.size();
            int[] avg = {avgX, avgY};
            filteredIntersections.add(avg);
        }
        for (int i : remainIds) {
            filteredIntersections.add(intersections.get(i));
        }
        return filteredIntersections;
    }

    public static Mat filterHoughLines(Mat lines, int min_threshold, int max_threshold) {
        double slope_threshold = Math.PI / 5;
        int position_threshold = 20;
        Mat filtered_lines = new Mat();
        List<Integer> used_id = new ArrayList<>();
        if (lines != null) {
            for (int i = 0; i < lines.rows(); i++) {
                if (used_id.contains(i)) {
                    continue;
                }
                double[] line = lines.get(i, 0);
                double rho = line[0];
                double theta = line[1];
                int count = 0;
                List<Integer> local_used_ids = new ArrayList<>();
                for (int j = 0; j < lines.rows(); j++) {
                    double[] other_line = lines.get(j, 0);
                    double other_rho = other_line[0];
                    double other_theta = other_line[1];
                    if (i == j || (Math.abs(theta - other_theta) < slope_threshold && Math.abs(rho - other_rho) < position_threshold)) {
                        local_used_ids.add(j);
                        count++;
                    }
                }
                if (count >= min_threshold && count <= max_threshold) {
                    filtered_lines.push_back(new MatOfDouble(line));
                }
                used_id.addAll(local_used_ids);
            }
        }
        return filtered_lines;
    }

    public static List<int[][]> filterCartesianLines(List<int[][]> cartesian_lines, int min_threshold, int max_threshold) {
        // Filter out lines that do not have at least 2 other lines with a similar slant
        double slope_threshold = Math.PI / 5;
        double position_threshold = 80;

        List<int[][]> filtered_lines = new ArrayList<>();
        Set<Integer> used_id = new HashSet<>();
        if (cartesian_lines != null) {
            for (int i = 0; i < cartesian_lines.size(); i++) {
                if (used_id.contains(i)) {
                    continue;
                }
                int[][] line = cartesian_lines.get(i);

                int count = 0;
                List<Integer> local_used_ids = new ArrayList<>();
                for (int j = 0; j < cartesian_lines.size(); j++) {
                    int[][] other_line = cartesian_lines.get(j);
                    double angle_diff = Math.atan2(line[0][1] - line[1][1], line[0][0] - line[1][0]) - Math.atan2(other_line[0][1] - other_line[1][1], other_line[0][0] - other_line[1][0]);
                    angle_diff = angle_diff % Math.PI;
                    if (angle_diff > Math.PI / 2) {
                        angle_diff -= Math.PI;
                    }

                    double[] distances = new double[4];
                    for (int k = 0; k < 4; k++){
                        distances[k] = Math.sqrt(Math.pow(line[k / 2][0]-other_line[k % 2][0], 2) + Math.pow(line[k / 2][1]-other_line[k % 2][1], 2));
                    }
                    double position_diff = Math.min(distances[0] + distances[2], distances[1] + distances[3]);
                    if (i == j || (Math.abs(angle_diff) < slope_threshold && position_diff < position_threshold)) {
                        local_used_ids.add(j);
                        count++;
                    }
                }

                // Add the line to the filtered list if it has at least 5 other lines with a similar slant
                if (count >= min_threshold && count <= max_threshold) {
                    List<int[][]> current_lines = new ArrayList<>();
                    for (int id : local_used_ids) {
                        current_lines.add(cartesian_lines.get(id));
                    }
                    int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
                    for (int[][] current_line : current_lines) {
                        x1 += current_line[0][0];
                        y1 += current_line[0][1];
                        x2 += current_line[1][0];
                        y2 += current_line[1][1];
                    }
                    int[][] avg_line = {{x1 / current_lines.size(), y1 / current_lines.size()}, {x2 / current_lines.size(), y2 / current_lines.size()}};
                    filtered_lines.add(avg_line);
                }
                used_id.addAll(local_used_ids);
            }
        }
        return filtered_lines;
    }

    public static List<int[][]> convertLinesToCartesian(Mat lines, Size size) {
        List<int[][]> cartesianLines = new ArrayList<>();
        for (int i = 0; i < lines.rows(); i++) {
            double[] data = lines.get(i, 0);
            double rho = data[0];
            double theta = data[1];
            double cosTheta = Math.cos(theta);
            double sinTheta = Math.sin(theta);
            int x1 = (int) (rho * cosTheta);
            int y1 = (int) (rho * sinTheta);
            int x2 = (int) (x1 - 1000 * sinTheta);
            int y2 = (int) (y1 + 1000 * cosTheta);



            cartesianLines.add(matchLineToEdge(x1, y1, x2, y2, size));
        }

        return cartesianLines;
    }

    public static int[][] matchLineToEdge(int x1, int y1, int x2, int y2, Size size) {
        double radians = Math.atan2(y2 - y1, x2 - x1);
        radians = ((radians % Math.PI) + Math.PI) % Math.PI;

        int width = (int)size.width;
        int height = (int)size.height;

        if (Math.abs(radians - (Math.PI / 2)) < 10e-3) {
            return new int[][]{ {x1, 0}, {x1, height} };
        }

        double m = Math.tan(radians);
        double b = y1 - m * x1;
        if (Math.abs(m) < 1){
            return new int[][]{ {0, (int)b}, {width, (int)(width*m+b)} };
        }
        else{
            return new int[][]{ {(int)((-b)/m), 0}, {(int)((height-b)/m), height} };
        }
    }

    private static Map<String, List<int[][]>> splitLinesAndRemoveOutliers(List<int[][]> cartesianLines){
        Map<String, List<int[][]>> lineGroups = new HashMap<>();
        lineGroups.put("vertical", new ArrayList<int[][]>());
        lineGroups.put("horizontal", new ArrayList<int[][]>());

        for (int[][] line : cartesianLines) {
            double theta = Math.atan2(line[1][1] - line[0][1], line[1][0] - line[0][0]);
            if (theta % Math.PI > Math.PI / 4 && theta % Math.PI < Math.PI * 3 / 4) {
                lineGroups.get("vertical").add(line);
            }
            else{
                lineGroups.get("horizontal").add(line);
            }
        }
//        for (String groupKey : lineGroups.keySet()) {
//            List<int[][]> groupLines = lineGroups.get(groupKey);
//            List<Integer> outliers = getOutliers(groupLines.stream()
//                    .map(line -> {
//                        double angle = Math.atan2(line[0][1] - line[1][1], line[0][0] - line[1][0]);
//                        return (int) Math.toDegrees((((angle % (Math.PI / 2)) + Math.PI) % (Math.PI / 2)));
//                    }).collect(Collectors.toList()));
//            for (int i : outliers) {
//                groupLines.remove(i);
//            }
//        }
        for (String groupKey : lineGroups.keySet()) {
            List<int[][]> groupLines = lineGroups.get(groupKey);
            List<Integer> angles = new ArrayList<Integer>();
            for (int[][] line : groupLines) {
                double angle = Math.atan2(line[0][1] - line[1][1], line[0][0] - line[1][0]);
                angles.add((int) Math.toDegrees((((angle % (Math.PI / 2)) + Math.PI) % (Math.PI / 2))));
            }
            List<Integer> outliers = getOutliers(angles);
            for (int i : outliers) {
                groupLines.remove(i);
            }
        }

        return lineGroups;
    }

    private static List<int[]> getIntersections(Map<String, List<int[][]>> lineGroups){
        List<int[][]> group1 = lineGroups.get("vertical");
        List<int[][]> group2 = lineGroups.get("horizontal");
        List<int[]> intersections = new ArrayList<>();
        for (int[][] line1 : group1) {
            for (int[][] line2 : group2) {
                double x1 = line1[0][0], y1 = line1[0][1], x2 = line1[1][0], y2 = line1[1][1];
                double x3 = line2[0][0], y3 = line2[0][1], x4 = line2[1][0], y4 = line2[1][1];

                double denominator = (y4-y3)*(x2-x1)-(x4-x3)*(y2-y1);
                if (denominator != 0) {
                    double ua = ((x4-x3)*(y1-y3)-(y4-y3)*(x1-x3))/denominator;
                    double ub = ((x2-x1)*(y1-y3)-(y2-y1)*(x1-x3))/denominator;
                    if (ua >= 0 && ua <= 1 && ub >= 0 && ub <= 1) {
                        int x = (int) (x1 + ua * (x2 - x1));
                        int y = (int) (y1 + ua * (y2 - y1));
                        intersections.add(new int[]{x, y});
                    }
                }
            }
        }
        return intersections;
    }

    public static List<int[]> getAllIntersections(List<int[]> intersections) {
        int numRows = 8; // 8 rows in chess board
        int numCols = 8; // 8 columns in chess board
        int numExpectedPoints = (numRows + 1) * (numCols + 1);
        if (intersections.size() < numExpectedPoints){
            return intersections;
        }

        // Convert intersections to DoublePoint array for use with KMeans
        DoublePoint[] intersectionsArr = new DoublePoint[intersections.size()];
        for (int i = 0; i < intersections.size(); i++) {
            intersectionsArr[i] = new DoublePoint(intersections.get(i));
        }

        // Use KMeans to cluster the intersection points
        KMeansPlusPlusClusterer<DoublePoint> kMeansPlusPlusClusterer = new KMeansPlusPlusClusterer<>(numExpectedPoints, 300);
        List<CentroidCluster<DoublePoint>> clusters = kMeansPlusPlusClusterer.cluster(Arrays.asList(intersectionsArr));

        // Get the cluster centers as the new intersection points
        List<int[]> allIntersections = new ArrayList<>();
        for (CentroidCluster<DoublePoint> cluster : clusters) {
            double[] intersection = cluster.getCenter().getPoint();
            int[] intersectionIntegers = new int[]{(int)intersection[0], (int)intersection[1]};
            allIntersections.add(intersectionIntegers);
        }
        return allIntersections;
    }

    public static List<Integer> getOutliers(List<Integer> values) {
        if (values.isEmpty()) {
            return Collections.emptyList();
        }

        int[] intArray = new int[values.size()];
        for (int i = 0; i < values.size(); i++) {
            intArray[i] = values.get(i);
        }

        // Calculate the upper and lower whiskers
        double[] quartiles = getQuartiles(intArray); // values.stream().mapToInt(v -> v).toArray());
        double q1 = quartiles[0];
        double q3 = quartiles[2];
        double iqr = q3 - q1;
        double upperWhisker = q3 + 1.5 * iqr;
        double lowerWhisker = q1 - 1.5 * iqr;

        // Identify the out-of-range samples
        List<Integer> outliers = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            double sample = values.get(i);
            if (sample > upperWhisker || sample < lowerWhisker) {
                outliers.add(i);
            }
        }
        return outliers;
    }

    private static double[] getQuartiles(int[] arr) {
        Arrays.sort(arr);
        double q1 = getMedian(Arrays.copyOfRange(arr, 0, arr.length / 2));
        double q2 = getMedian(arr);
        double q3 = getMedian(Arrays.copyOfRange(arr, (arr.length + 1) / 2, arr.length));
        return new double[]{q1, q2, q3};
    }

    private static double getMedian(int[] arr) {
        int middle = arr.length / 2;
        if (arr.length % 2 == 0) {
            return (arr[middle - 1] + arr[middle]) / 2.0;
        } else {
            return arr[middle];
        }
    }
}
