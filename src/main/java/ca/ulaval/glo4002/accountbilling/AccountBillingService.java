package ca.ulaval.glo4002.accountbilling;

import java.util.List;

public class AccountBillingService {

    public void cancelInvoiceAndRedistributeFunds(BillId billId) {
        Bill billToCancel = findBillById(billId);
        if (billToCancel == null) {
            throw new BillNotFoundException();
        }
        ClientId clientId = billToCancel.getClientId();

        if (!billToCancel.isCancelled()) {
            billToCancel.cancel();
        }
        persistBill(billToCancel);

        List<Allocation> allocationsToRedistribute = billToCancel.getAllocations();

        for (Allocation allocationToRedistribute : allocationsToRedistribute) {
            List<Bill> clientBills = findAllBillsByClient(clientId);
            int amountToRedistribute = allocationToRedistribute.getAmount();

            for (Bill billCandidate : clientBills) {
                if (billToCancel != billCandidate) {
                    int remainingAmount = billCandidate.getRemainingAmount();
                    Allocation newAllocation;
                    if (remainingAmount <= amountToRedistribute) {
                        newAllocation = new Allocation(remainingAmount);
                        amountToRedistribute -= remainingAmount;
                    } else {
                        newAllocation = new Allocation(amountToRedistribute);
                        amountToRedistribute = 0;
                    }

                    billCandidate.addAllocation(newAllocation);

                    persistBill(billCandidate);
                }

                if (amountToRedistribute == 0) {
                    break;
                }
            }
        }
    }

    protected List<Bill> findAllBillsByClient(ClientId clientId) {
        return BillDAO.getInstance().findAllByClient(clientId);
    }

    protected void persistBill(Bill bill) {
        BillDAO.getInstance().persist(bill);
    }

    protected Bill findBillById(BillId billId) {
        return BillDAO.getInstance().findBill(billId);
    }
}
