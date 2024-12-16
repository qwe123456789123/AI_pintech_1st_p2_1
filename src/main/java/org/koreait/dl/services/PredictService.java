package org.koreait.dl.services;

// 필요한 클래스 및 애너테이션 임포트
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

/**
 * PredictService 클래스는 Python 스크립트를 실행하여 예측 작업을 처리합니다.
 * - Spring의 `@Profile("dl")`을 사용하여 "dl" 프로파일에서만 활성화됩니다.
 */
@Service // Spring의 서비스 계층 컴포넌트로 등록
@Profile("dl") // "dl" 프로파일에서만 이 서비스가 활성화되도록 설정
public class PredictService {

    /**
     * Python 실행 파일 경로.
     * 설정 파일에서 `python.run.path` 속성을 주입받습니다.
     */
    @Value("${python.run.path}")
    private String runPath;

    /**
     * Python 스크립트 파일 경로.
     * 설정 파일에서 `python.script.path` 속성을 주입받습니다.
     */
    @Value("${python.script.path}")
    private String scriptPath;

    /**
     * 데이터 URL 경로.
     * 설정 파일에서 `python.data.url` 속성을 주입받습니다.
     */
    @Value("${python.data.url}")
    private String dataUrl;

    /**
     * Jackson의 ObjectMapper 인스턴스를 자동 주입받습니다.
     * - Java 객체를 JSON 형식으로 변환하거나 그 반대로 변환하는 데 사용됩니다.
     */
    @Autowired
    private ObjectMapper om;

    /**
     * Python 스크립트를 실행하여 예측 작업을 수행합니다.
     * - 데이터를 JSON 형식으로 직렬화하여 Python 스크립트에 전달합니다.
     * - Python 스크립트로부터 예측 결과를 수신합니다.
     *
     * @param items 예측에 사용할 입력 데이터 (List<int[]> 형식)
     * @return 예측 결과 (int 배열), 실패 시 `null` 반환
     */
    public int[] predict(List<int[]> items) {
        try {
            // 입력 데이터를 JSON 형식으로 변환
            String data = om.writeValueAsString(items);

            // ProcessBuilder를 사용하여 Python 스크립트를 실행
            ProcessBuilder builder = new ProcessBuilder(
                    runPath, // Python 실행 경로
                    scriptPath + "predict.py", // 실행할 스크립트 경로
                    dataUrl + "?mode=ALL", // 데이터 URL
                    data // JSON 데이터
            );

            // 프로세스 시작
            Process process = builder.start();

            // Python 스크립트의 출력 스트림 읽기
            InputStream in = process.getInputStream();

            // 출력 스트림 데이터를 JSON 형식에서 int 배열로 변환
            return om.readValue(in.readAllBytes(), int[].class);

        } catch (Exception e) {
            // 예외 발생 시 스택 트레이스를 출력
            e.printStackTrace();
        }

        // 예외 발생 시 null 반환
        return null;
    }
}
