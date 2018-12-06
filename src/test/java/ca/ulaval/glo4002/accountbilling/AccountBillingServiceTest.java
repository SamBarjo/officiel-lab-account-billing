package ca.ulaval.glo4002.accountbilling;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AccountBillingServiceTest {
    private AccountBillingService accountBillingService = new TestableAccountBillingService();
    private Bill bill = null;
    private BillId billId = new BillId(0);
    private List<Bill> persistedBills = new ArrayList<>();

    @Test(expected = BillNotFoundException.class)
    public void billToCancelIsNull_ThrowBillNotFoundException() {
        accountBillingService.cancelInvoiceAndRedistributeFunds(billId);
    }

    @Test
    public void nonCancelledBill_billIsCancelled() {
        bill = new Bill(null, 0);

        accountBillingService.cancelInvoiceAndRedistributeFunds(billId);

        assertTrue(bill.isCancelled());
    }

    @Test
    public void cancelledBill_billIsNotCancelled() {
        bill = new Bill(null, 0);
        bill.cancel();

        accountBillingService.cancelInvoiceAndRedistributeFunds(billId);

        assertFalse(bill.isCancelled());
    }

    private class TestableAccountBillingService extends AccountBillingService {
        @Override
        protected Bill findBillById(BillId billId) {
            return bill;
        }

        @Override
        protected void persistBill(Bill bill) {
            persistedBills.add(bill);
        }
    }
}
