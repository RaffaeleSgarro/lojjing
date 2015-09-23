package lojjing;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class AbbreviateInvocationTest extends TestCase {

    private Event target;
    private String src;

    @Test
    public void test1() {
        thisOne("model.recipe.RecipeService.backToQueue(java.lang.String,java.util.List,model.recipe.IRecipeService$IQueuePropertyOverride) (return void)(a:430)");
        becomes("m.r.RecipeService.backToQueue:430");
    }

    @Test
    public void test2() {
        thisOne("print.model.impl.ApaPrintService.getFilledPojo() (return print.model.pojo.ApaPojo)(a:58)");
        becomes("p.m.i.ApaPrintService.getFilledPojo:58");
    }

    @Test
    public void test3() {
        thisOne("org.jumpmind.symmetric.service.impl.DataLoaderService$ManageIncomingBatchListener.batchInError(DataLoaderService.java:891)");
        becomes("o.j.s.s.i.DataLoaderService$ManageIncomingBatchListener.batchInError:891");
    }

    @Test
    public void test4() {
        thisOne("presenter.environment.patient.fse.Parcel.store() (return void)(a:46)");
        becomes("p.e.p.f.Parcel.store:46");
    }

    @Test
    public void test5() {
        thisOne("presenter.reports.query.PianiApaAttivi.findLotOwnedBy(org.jooq.DSLContext,java.lang.String) (return java.lang.String)(a:94)");
        becomes("p.r.q.PianiApaAttivi.findLotOwnedBy:94");
    }

    @Test
    public void test6() {
        thisOne("gui.view.shared.PatientAge.toString(java.util.Date,java.util.Date) (return java.lang.String)(a:40)");
        becomes("g.v.s.PatientAge.toString:40");
    }


    @Test
    public void test7() {
        thisOne("device.command.siss.sign.RecipeToRequest.processRecipeDrugRecords(java.util.ArrayList) (return void)(a:200)");
        becomes("d.c.s.s.RecipeToRequest.processRecipeDrugRecords:200");
    }

    private void thisOne(String src) {
        this.src = src;
    }

    private void becomes(String expected) {
        assertEquals(target.abbreviateInvocation(src), expected);
    }

    @BeforeMethod
    public void setUp() {
        src = null;
        target = new Event(0, "", "");
    }

}