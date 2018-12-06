package ca.ulaval.glo4002.accountbilling;

import java.util.List;

public class AccountBillingService {

    public void cancelInvoiceAndRedistributeFunds(BillId billId) {
        Bill billToCancel = BillDAO.getInstance().findBill(billId);
        if (billToCancel != null) {
            ClientId clientId = billToCancel.getClientId();

            if (!billToCancel.isCancelled()) {
                billToCancel.cancel();
            }
            BillDAO.getInstance().persist(billToCancel);

            List<Allocation> allocationList = billToCancel.getAllocations();

            for (Allocation currentAllocation : allocationList) {
                List<Bill> clientBills = BillDAO.getInstance().findAllByClient(clientId);
                int currentAmount = currentAllocation.getAmount();

                for (Bill bill : clientBills) {
                    if (billToCancel != bill) {
                        int remainingAmount = bill.getRemainingAmount();
                        Allocation newAllocation;
                        if (remainingAmount <= currentAmount) {
                            newAllocation = new Allocation(remainingAmount);
                            currentAmount -= remainingAmount;
                        } else {
                            newAllocation = new Allocation(currentAmount);
                            currentAmount = 0;
                        }

                        bill.addAllocation(newAllocation);

                        BillDAO.getInstance().persist(bill);
                    }

                    if (currentAmount == 0) {
                        break;
                    }
                }
            }
        } else {
            throw new BillNotFoundException();
        }
    }
}
