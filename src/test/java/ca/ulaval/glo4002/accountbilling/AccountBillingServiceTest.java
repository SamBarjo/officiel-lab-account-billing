package ca.ulaval.glo4002.accountbilling;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AccountBillingServiceTest {
    private AccountBillingService accountBillingService = new TestableAccountBillingService();
    private Bill bill = null;
    private BillId billId = new BillId(0);
    private List<Bill> persistedBills = new ArrayList<>();
    private int cancelCount = 0;
    private Allocation allocation = new Allocation(5);
    private List<Bill> clientBills = new ArrayList<>();

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
        bill = new CountingBill(null, 0);
        bill.cancel();

        accountBillingService.cancelInvoiceAndRedistributeFunds(billId);

        assertEquals(1, cancelCount);
    }

    @Test
    public void anyBill_billIsPersisted() {
        bill = new Bill(null, 0);

        accountBillingService.cancelInvoiceAndRedistributeFunds(billId);

        assertTrue(persistedBills.contains(bill));
    }

    @Test
    public void billCandidateIsBillToCancel_allocationIsNotAdded() {
        bill = new Bill(null, 0);
        bill.addAllocation(allocation);
        clientBills.add(bill);

        accountBillingService.cancelInvoiceAndRedistributeFunds(billId);

        assertEquals(1, bill.getAllocations().size());
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

        @Override
        protected List<Bill> findAllBillsByClient(ClientId clientId) {
            return clientBills;
        }
    }

    private class CountingBill extends Bill {
        public CountingBill(ClientId clientId, int total) {
            super(clientId, total);
        }

        @Override
        public void cancel() {
            super.cancel();
            cancelCount++;
        }
    }
}
