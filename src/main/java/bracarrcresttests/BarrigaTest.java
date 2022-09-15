package bracarrcresttests;

import bracarrcrestcore.BaseTest;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.omg.CORBA.PRIVATE_MEMBER;
import utils.DateUtils;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BarrigaTest extends BaseTest {

    private String TOKEN;

    private static String CONTA_NAME = "Conta " + System.nanoTime();
    private static Integer CONTA_ID;
    private static Integer MOV_ID;




    @Before
    public void login(){
        Map<String, String> login = new HashMap<>();
        login.put("email", "amaro@costa");
        login.put("senha", "123456");

        TOKEN  = given()
                .body(login)
                .contentType(ContentType.JSON)
                .when()
                .post("/signin")
                .then()
                .statusCode(200)
                .extract()
                .path("token")
                ;


}
    @Test
    public void t01_naoDeverAcesssarAPISemToken(){
               given()
               .when()
                       .get("/contas")
               .then()
                       .statusCode(401)
               ;
    }
    @Test
    public void t02_deveIncluirContComSucesso() {


        CONTA_ID = given()
                .header("Authorization", "JWT " + TOKEN)

                .body("{\"nome\": \""+CONTA_NAME+"\"}")
                .contentType(ContentType.JSON)
        .when()
                .post("/contas")
        .then()
                .statusCode(201)
                .extract().path("id")
        ;
    }
        @Test
        public void t03_deveAlterarContaComSucesso() {

            given()
                    .header("Authorization", "JWT " + TOKEN)
                    .body("{\"nome\": \""+CONTA_NAME+"contaalterada\"}")
                    .contentType(ContentType.JSON)
                    .pathParam("id", CONTA_ID)
            .when()
                    .put("/contas/{id}")
            .then()
                    .statusCode(200)
                    .body("nome", is(CONTA_NAME+"contaalterada"))
            ;
    }
    @Test
    public void t04_naoDeveInserieContaComMesmoNome() {

        given()
                .header("Authorization", "JWT " + TOKEN)
                .body("{\"nome\": \""+CONTA_NAME+"contaalterada\"}")
                .contentType(ContentType.JSON)
        .when()
                .post("/contas")
        .then()
                .statusCode(400)
                .body("error", is("Já existe uma conta com esse nome!"))
        ;
    }
    @Test
    public void t05_deveInserieMovimentacaoSucesso() {
        Movimentacao mov = getMovimentacaoValida();

       MOV_ID = given()
                .header("Authorization", "JWT " + TOKEN)
                .body(mov)
                .contentType(ContentType.JSON)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(500)
                .extract().path("id")

        ;
    }

    @Test
    public void t06_deveValidarCampoObrigatorioMovimentacao() {

        given()
                .header("Authorization", "JWT " + TOKEN)
                .body("{}")
                .contentType(ContentType.JSON)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(400)
                .body("$", hasSize(8))
                .body("msg", hasItems(
                        "Data da Movimentação é obrigatório",
                        "Data do pagamento é obrigatório",
                        "Descrição é obrigatório",
                        "Interessado é obrigatório",
                        "Valor é obrigatório",
                        "Valor deve ser um número",
                        "Conta é obrigatório",
                        "Situação é obrigatório"
                ))


        ;
    }
    @Test
    public void t07_nãoDeveInserieMovimentacaoSucessoComDataFutura() {
        Movimentacao mov = getMovimentacaoValida();
        mov.setData_transacao(DateUtils.getDataDiferencaDias(2));

        given()
                .header("Authorization", "JWT " + TOKEN)
                .body(mov)
                .contentType(ContentType.JSON)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(400)
                .body("$",hasSize(1))
                .body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))

        ;
    }


    private Movimentacao getMovimentacaoValida(){
        Movimentacao mov = new Movimentacao();
        mov.setConta_id(CONTA_ID);
        //   mov.setUsuario_id(usuario_id);
        mov.setDescricao("Descricao da movimentacao");
        mov.setEnvolvido("Envolvido na mov");
        mov.setData_transacao(DateUtils.getDataDiferencaDias(-1));
        mov.setData_pagamento(DateUtils.getDataDiferencaDias(5));
        mov.setValor(100f);
        mov.setStatus(true);
        return mov;
    }
}

