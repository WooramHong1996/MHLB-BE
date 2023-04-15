package com.gigajet.mhlb.global.common.util;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class TimeCheckAop {
    /**Around : 실행 전후로 사용할 로직이 있는경우 사용
     * Around("execution(* com.gigajet.mhlb.domain..*Controller.*(..))")
     * <br>
     * excution > 메소드 실행시점을 의미
     * <br><br>
     * 첫번째 * > 모든 타입
     * <br>ex)void:보이드만 적용 <br>!String: 스트링이 아닌 모든 메소드에 적용
     * <br><br>
     * com.gigajet.mhlb.domain.. > domain과 그 하위 패키지까지 선택
     * <br>
     * ex) com.gigajet : 기가젯 하위 모든 패키지
     * <br>com.gigajet..mail : 기가젯 하위 모든 패키지중 mail 패키지
     * <br><br>
     * ..뒤의 *Controller : Controller로 끝나는 모든 클래스
     * <br>ex) * : 모든 클래스
     * <br>UserController+ : 유저 컨트롤러에서 파생된 모든 클래스&인터페이스
     * <br><br>
     * 컨트롤러 뒤의 .* : 모든 메소드
     * <br>ex) get* : get로 시작하는 모든 메소드
     * <br><br>
     * (..) : 모든 매개변수 선택
     * <br>ex) (*) : 매개변수가 1개인 메소드
     * <br>(com.gigajet.mhlb.domain.status.entity.SqlStatus,*,..) : 최소 2개 이상의 매개변수를 갖고 하나는 SqlStatus객체가 반드시 포함되어야함
     * <br><strong>클래스 객체를 포함해야 하는경우 패키지 경로를 포함해주어야함</strong>
     * @author Galmaeki
     */
//    @Around("execution(* com.gigajet.mhlb.domain..*Controller.*(..))")
//    @Around("execution(* com.gigajet..*(..))")
    public Object timeChecker(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        log.info("Start : " + joinPoint.toString());
        try {
            return joinPoint.proceed();
        } finally {
            long end = System.currentTimeMillis();
            long time = end - start;
            log.info("End : " + joinPoint.toString() + " -> " + time + "ms");
        }
    }
}
