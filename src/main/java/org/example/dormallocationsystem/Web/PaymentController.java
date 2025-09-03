package org.example.dormallocationsystem.Web;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.example.dormallocationsystem.Domain.Payment;
import org.example.dormallocationsystem.Domain.Student;
import org.example.dormallocationsystem.Repository.PaymentRepository;
import org.example.dormallocationsystem.Repository.StudentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    public PaymentController(PaymentRepository paymentRepository, StudentRepository studentRepository) {
        this.paymentRepository = paymentRepository;
        this.studentRepository = studentRepository;
    }

    @PostMapping("/create-checkout-session")
    @ResponseBody
    public Map<String, Object> createCheckoutSession(
            @RequestParam Long studentId,
            @RequestParam("paymentMonths") List<String> paymentMonths) throws StripeException {

        Stripe.apiKey = stripeApiKey;

        int amount = 50 * paymentMonths.size() * 100;

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://localhost:8080/payment/success?studentId="
                                + studentId + "&paymentMonths=" + String.join(",", paymentMonths))
                        .setCancelUrl("http://localhost:8080/dashboard?studentId=" + studentId)
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency("eur")
                                                        .setUnitAmount((long) amount)
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        .setName("Dorm Room Rent for "  + String.join(", ", paymentMonths))
                                                                        .build())
                                                        .build())
                                        .build())
                        .build();

        Session session = Session.create(params);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("id", session.getId());

        return responseData;
    }

    @GetMapping("/success")
    public String paymentSuccess(@RequestParam Long studentId, @RequestParam String paymentMonths) {
        Student student = studentRepository.findById(studentId).orElseThrow();

        for (String month : paymentMonths.split(",")) {
            Payment payment = new Payment();
            payment.setAmount(50);
            payment.setPaymentDate(LocalDate.now());
            payment.setPaymentMonth(month);
            payment.setStudent(student);
            paymentRepository.save(payment);
        }


        return "redirect:/dashboard?studentId=" + studentId;
    }
}
