package hooyn.unknown;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_java;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.helper.opencv_imgcodecs.cvSaveImage;

@Slf4j
public class opencv {

    public static void main(String[] args) throws InterruptedException {

//        IplImage iplImage = cvLoadImage("C:/Users/twim/Desktop/response.png");
//        IplImage threshold = cvCreateImage(cvGetSize(iplImage), 8, 1);
//        cvInRangeS(iplImage, cvScalar(155, 155, 155, 0), cvScalar(255, 255, 255, 0), threshold);
//
//        //cvSmooth(threshold, iplImage);
//        cvSaveImage("C:/Users/twim/Desktop/response1.png", threshold);

        OpenCVFrameConverter.ToMat toMat = new OpenCVFrameConverter.ToMat();

        Mat mat = opencv_imgcodecs.imread("C:/Users/twim/Desktop/response3.png");
        opencv_imgproc.cvtColor(mat, mat, opencv_imgproc.COLOR_BGR2HSV);

        opencv_core.inRange(mat, new Mat(1, 1, CV_32SC4, new Scalar(25, 40, 0, 0)),
                                new Mat(1, 1, CV_32SC4, new Scalar(85, 255, 255, 0)), mat);

        opencv_imgcodecs.imwrite("C:/Users/twim/Desktop/response_vvv.png", mat);

        Loader.load(opencv_java.class);

        org.opencv.core.Mat src = Imgcodecs.imread("C:/Users/twim/Desktop/response_vvv.png");
        //Converting the source image to binary
        org.opencv.core.Mat gray = new org.opencv.core.Mat(src.rows(), src.cols(), src.type());
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        org.opencv.core.Mat binary = new org.opencv.core.Mat(src.rows(), src.cols(), src.type(), new org.opencv.core.Scalar(0));
        Imgproc.threshold(gray, binary, 100, 255, Imgproc.THRESH_BINARY);
        //Finding Contours
        List<MatOfPoint> contours = new ArrayList<>();
        org.opencv.core.Mat hierarchey = new org.opencv.core.Mat();
        Imgproc.findContours(binary, contours, hierarchey, Imgproc.RETR_TREE,
                Imgproc.CHAIN_APPROX_SIMPLE);
        //Drawing the Contours
        org.opencv.core.Scalar color = new org.opencv.core.Scalar(0, 0, 255);

//        Imgproc.drawContours(src, contours, -1, color, 2, Imgproc.LINE_8,
//                hierarchey, 2, new Point() ) ;

        int i = 1;
        for (MatOfPoint contour : contours) {

            Rect rect = Imgproc.boundingRect(contour);


            if(rect.height > 30 && rect.width > 50) {
                String data = "W:" + rect.width + "/H:" + rect.height + "";
                Imgproc.rectangle(src, rect, new org.opencv.core.Scalar(0, 255, 0));

                Imgproc.putText(src, data, new Point(rect.x, rect.y-20), Imgproc.FONT_HERSHEY_TRIPLEX, 0.5, new org.opencv.core.Scalar(0, 0, 255));
            }
        }

        //BufferedImage image = toBufferedImage(mat);

        CanvasFrame canvas = new CanvasFrame("CANVAS", 1);
        Frame converted = toMat.convert(src);
        canvas.showImage(converted);

    }

    public static BufferedImage toBufferedImage(Mat mat) {
        // Make sure that FrameConverters and JavaCV Frame are properly closed
        try (OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat()) {
            try (Frame frame = converter.convert(mat)) {
                try (Java2DFrameConverter java2DConverter = new Java2DFrameConverter()) {
                    return java2DConverter.convert(frame);
                }
            }
        }
    }

    public void save(File file, Mat image) {
        imwrite(file.getAbsolutePath(), image);
    }
}
