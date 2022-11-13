package prgms.vouchermanagementapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import prgms.vouchermanagementapp.io.CommandType;
import prgms.vouchermanagementapp.io.IOManager;
import prgms.vouchermanagementapp.model.Amount;
import prgms.vouchermanagementapp.model.Ratio;
import prgms.vouchermanagementapp.voucher.VoucherManager;
import prgms.vouchermanagementapp.voucher.VoucherType;

import java.text.MessageFormat;
import java.util.Optional;

@Component
public class CommandExecutor {

    private static final Logger log = LoggerFactory.getLogger(CommandExecutor.class);

    private final IOManager ioManager;
    private final VoucherManager voucherManager;
    private final RunningState runningState;

    public CommandExecutor(IOManager ioManager, VoucherManager voucherManager) {
        this.ioManager = ioManager;
        this.voucherManager = voucherManager;
        this.runningState = new RunningState();
    }

    public void run() {
        while (runningState.isRunning()) {
            String command = ioManager.askCommand();

            try {
                Optional<CommandType> commandType = CommandType.of(command);
                commandType.ifPresent(this::executeCommand);
            } catch (IllegalArgumentException e) {
                log.warn("command input error occurred: {}", e.getMessage());
                ioManager.notifyErrorOccurred(e.getMessage());
            }
        }
    }

    public void executeCommand(CommandType commandType) {
        if (commandType.is(CommandType.EXIT)) {
            runExit();
            return;
        }

        if (commandType.is(CommandType.CREATE)) {
            runCreate();
            return;
        }

        if (commandType.is(CommandType.LIST)) {
            runList();
        }

        if (commandType.is(CommandType.BLACKLIST)) {
            runBlacklist();
        }
    }

    private void runExit() {
        ioManager.notifyExit();
        runningState.exit();
    }

    private void runCreate() {
        Optional<VoucherType> voucherType = askVoucherType();
        voucherType.ifPresent(this::requestVoucherCreation);
    }

    private Optional<VoucherType> askVoucherType() {
        String voucherTypeIndex = ioManager.askVoucherTypeIndex();

        try {
            VoucherType voucherType = VoucherType.of(voucherTypeIndex);
            return Optional.of(voucherType);
        } catch (IllegalArgumentException exception) {
            ioManager.notifyErrorOccurred(MessageFormat.format("index ''{0}'' is invalid!!!", voucherTypeIndex));
            return Optional.empty();
        }
    }

    private void requestVoucherCreation(VoucherType voucherType) {
        if (voucherType.is(VoucherType.FixedAmountVoucher)) {
            Optional<Amount> fixedDiscountAmount = ioManager.askFixedDiscountAmount();
            fixedDiscountAmount.ifPresent(voucherManager::createVoucher);
        }

        if (voucherType.is(VoucherType.PercentDiscountVoucher)) {
            Optional<Ratio> fixedDiscountRatio = ioManager.askFixedDiscountRatio();
            fixedDiscountRatio.ifPresent(voucherManager::createVoucher);
        }
    }

    private void runList() {
        ioManager.notifyVouchers(voucherManager.findAllVouchers());
    }

    private void runBlacklist() {
        ioManager.showBlacklist();
    }
}
