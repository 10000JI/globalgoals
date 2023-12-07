package dev.globalgoals.controller;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/payment")
public class PaymentController {


    /**
     * Iamport 결제 검증 컨트롤러
     **/
    private final IamportClient iamportClient;

    // 생성자를 통해 REST API 와 REST API secret 입력
    public PaymentController() {
        this.iamportClient = new IamportClient("5556112308328228", "ki7QHaqaEDVk9ZEw2CiguDY5pOCGjOXV3stio4YrEjFbrhTbHN1hrrc477Msc0D9jx7Jg8DqsisAKPaj");
    }

    /**
     * 프론트에서 받은 PG사 결괏값을 통해 아임포트 토큰 발행
     **/
    @ResponseBody
    @RequestMapping("/validate/{imp_uid}")
    public IamportResponse<Payment> paymentByImpUid(@PathVariable String imp_uid) throws IamportResponseException, IOException {
        log.info("paymentByImpUid 진입");
        return iamportClient.paymentByImpUid(imp_uid); //paymentByImpUid(): 아임포트서버에서 imp_uid(거래 고유번호)를 검사하여, 데이터를 보내줍니다.
    }
}
