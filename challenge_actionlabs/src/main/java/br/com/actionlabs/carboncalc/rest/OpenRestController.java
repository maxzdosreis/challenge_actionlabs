package br.com.actionlabs.carboncalc.rest;

import br.com.actionlabs.carboncalc.dto.*;
import br.com.actionlabs.carboncalc.service.CalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/open")
@RequiredArgsConstructor
@Slf4j
public class OpenRestController {

  private final CalculationService calculationService;

  @PostMapping("start-calc")
  public ResponseEntity<StartCalcResponseDTO> startCalculation(
      @RequestBody StartCalcRequestDTO request) {
    StartCalcResponseDTO response = calculationService.startCalculation(request);
    return ResponseEntity.ok(response);
  }

  @PutMapping("info")
  public ResponseEntity<UpdateCalcInfoResponseDTO> updateInfo(
      @RequestBody UpdateCalcInfoRequestDTO request) {
    UpdateCalcInfoResponseDTO response = calculationService.updateCalculationInfo(request);

    if(!response.isSuccess()){
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(response);
  }

  @GetMapping("result/{id}")
  public ResponseEntity<CarbonCalculationResultDTO> getResult(@PathVariable String id) {
    try{
      CarbonCalculationResultDTO result = calculationService.calculateResult(id);
      return ResponseEntity.ok(result);
    }catch(RuntimeException ex){
      return ResponseEntity.notFound().build();
    }
  }
}
