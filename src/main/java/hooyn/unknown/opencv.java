package hooyn.unknown;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_java;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.helper.opencv_imgcodecs.cvSaveImage;

@Slf4j
public class opencv {

    public static void main(String[] args) {

        // Mat -> Frame Converter init
        OpenCVFrameConverter.ToMat toMat = new OpenCVFrameConverter.ToMat();

        // Local에 있는 File Mat으로 받아오기
        Mat mat = opencv_imgcodecs.imread("C:/Users/twim/Desktop/response3.png");

        // 이미지 1차 처리 BGR -> HSV (Hue, Saturation, Value)
        opencv_imgproc.cvtColor(mat, mat, opencv_imgproc.COLOR_BGR2HSV);

        // 이미지 필터 init -> 초록색 부분만 인식
        opencv_core.inRange(mat, new Mat(1, 1, CV_32SC4, new Scalar(25, 40, 0, 0)),
                                new Mat(1, 1, CV_32SC4, new Scalar(85, 255, 255, 0)), mat);

        // 이미지 필터를 적용한 image file 저장
        opencv_imgcodecs.imwrite("C:/Users/twim/Desktop/response_vvv.png", mat);

        // java.org.opencv 라이브러리 load
        Loader.load(opencv_java.class);

        // 저장한 이미지 다시 로드 org.opencv.core.Mat 으로 생성
        org.opencv.core.Mat src = Imgcodecs.imread("C:/Users/twim/Desktop/response_vvv.png");

        // 이미지 Gray 색상으로 처리 후 검은색과 흰색으로 나타내도록 변경
        org.opencv.core.Mat gray = new org.opencv.core.Mat(src.rows(), src.cols(), src.type());
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        org.opencv.core.Mat binary = new org.opencv.core.Mat(src.rows(), src.cols(), src.type(), new org.opencv.core.Scalar(0));
        Imgproc.threshold(gray, binary, 100, 255, Imgproc.THRESH_BINARY);


        List<MatOfPoint> contours = new ArrayList<>();
        org.opencv.core.Mat hierarchey = new org.opencv.core.Mat();

        // 윤곽선 찾아서 List 에 추가
        Imgproc.findContours(binary, contours, hierarchey, Imgproc.RETR_TREE,
                Imgproc.CHAIN_APPROX_SIMPLE);

        // 선으로 표시
        /*Imgproc.drawContours(src, contours, -1, color, 2, Imgproc.LINE_8,
                hierarchey, 2, new Point() ) ;*/

        // 윤곽선 마다 사각형으로 표시하기
        int i = 1;
        for (MatOfPoint contour : contours) {

            // 윤곽선 데이터에 따라 사각형 그리기
            Rect rect = Imgproc.boundingRect(contour);

            // 큰 사각형만 표시하기 [실제 필요한 데이터]
            if(rect.height > 30 && rect.width > 50) {
                String data = "W:" + rect.width + "/H:" + rect.height + "";
                Imgproc.rectangle(src, rect, new org.opencv.core.Scalar(0, 255, 0));

                // 해당 사각형 위에 Width 와 Height 추가
                Imgproc.putText(src, data, new Point(rect.x, rect.y-20), Imgproc.FONT_HERSHEY_TRIPLEX, 0.5, new org.opencv.core.Scalar(0, 0, 255));
            }
        }

        // Canvas 를 통해 결과물 이미지 받기
        CanvasFrame canvas = new CanvasFrame("Result", 1);
        Frame converted = toMat.convert(src);
        canvas.showImage(converted);
    }

    /**
     * IplImage Input 일 때 이미지 처리
     */
    public void processIplImage(IplImage iplImage) {

        // image create
        IplImage threshold = cvCreateImage(cvGetSize(iplImage), 8, 1);

        // 초록색 detect
        cvInRangeS(iplImage, cvScalar(155, 155, 155, 0), cvScalar(255, 255, 255, 0), threshold);

        // image save
        cvSaveImage("C:/Users/twim/Desktop/file.png", threshold);
    }

    /**
     * Mat File Image로 저장
     */
    public void save(File file, Mat image) {
        imwrite(file.getAbsolutePath(), image);
    }
}
