package prgms.vouchermanagementapp.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prgms.vouchermanagementapp.model.dto.VoucherCreationDto;
import prgms.vouchermanagementapp.model.dto.VoucherResponseDto;
import prgms.vouchermanagementapp.service.VoucherApiService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/vouchers")
public class VoucherApiController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final VoucherApiService voucherApiService;

    public VoucherApiController(VoucherApiService voucherApiService) {
        this.voucherApiService = voucherApiService;
    }

    @GetMapping
    public ResponseEntity<List<VoucherResponseDto>> findAllVouchers() {
        return ResponseEntity.ok()
                .body(voucherApiService.findAll());
    }

    @GetMapping("/{voucherId}")
    public ResponseEntity<VoucherResponseDto> findVoucherById(
            @PathVariable UUID voucherId
    ) {
        return ResponseEntity.ok()
                .body(voucherApiService.findById(voucherId));
    }

    @PostMapping
    public ResponseEntity<UUID> createVoucher(
            @RequestBody VoucherCreationDto voucherCreationDto
    ) {
        log.info("voucherCreationDto: {}", voucherCreationDto);
        return ResponseEntity.ok()
                .body(voucherApiService.save(voucherCreationDto));
    }
}
