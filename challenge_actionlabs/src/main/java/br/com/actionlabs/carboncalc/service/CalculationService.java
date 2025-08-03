package br.com.actionlabs.carboncalc.service;

import br.com.actionlabs.carboncalc.dto.*;
import br.com.actionlabs.carboncalc.model.*;
import br.com.actionlabs.carboncalc.repository.CalculationRepository;
import br.com.actionlabs.carboncalc.repository.EnergyEmissionFactorRepository;
import br.com.actionlabs.carboncalc.repository.SolidWasteEmissionFactorRepository;
import br.com.actionlabs.carboncalc.repository.TransportationEmissionFactorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalculationService {

    private final CalculationRepository calculationRepository;
    private final EnergyEmissionFactorRepository energyEmissionFactorRepository;
    private final SolidWasteEmissionFactorRepository solidWasteEmissionFactorRepository;
    private final TransportationEmissionFactorRepository transportationEmissionFactorRepository;

    // método que criar um novo cálculo
    public StartCalcResponseDTO startCalculation(StartCalcRequestDTO request){
        Calculation calculation = Calculation.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .uf(request.getUf())
                .build();

        calculation = calculationRepository.save(calculation);

        StartCalcResponseDTO response = new StartCalcResponseDTO();
        response.setId(calculation.getId());
        return response;
    }

    // método que atualiza os dados do cálculo
    public UpdateCalcInfoResponseDTO updateCalculationInfo(UpdateCalcInfoRequestDTO request){
        Optional<Calculation> calculationOptional = calculationRepository.findById(request.getId());
        if(calculationOptional.isEmpty()){
            UpdateCalcInfoResponseDTO response = new UpdateCalcInfoResponseDTO();
            response.setSuccess(false);
            return response;
        }

        Calculation calculation = calculationOptional.get();

        List<Transportation> transportationList = request.getTransportation().stream()
                .map(t -> Transportation.builder()
                        .type(t.getType())
                        .monthlyDistance(t.getMonthlyDistance())
                        .build())
                .collect(Collectors.toList());

        calculation.setEnergyConsumption(request.getEnergyConsumption());
        calculation.setTransportation(transportationList);
        calculation.setSolidWasteTotal(request.getSolidWasteTotal());
        calculation.setRecyclePercentage(request.getRecyclePercentage());

        calculationRepository.save(calculation);

        UpdateCalcInfoResponseDTO response = new UpdateCalcInfoResponseDTO();
        response.setSuccess(true);
        return response;
    }

    // Método que traz o resultado final
    public CarbonCalculationResultDTO calculateResult(String id){
        Calculation calculation = calculationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Calculation not found"));

        double energyEmission = calculateEnergyEmission(calculation);
        double transportEmission = calculateTransportationEmission(calculation);
        double solidWasteEmission = calculateSolidWasteEmission(calculation);

        CarbonCalculationResultDTO result = new CarbonCalculationResultDTO();
        result.setEnergy(energyEmission);
        result.setTransportation(transportEmission);
        result.setSolidWaste(solidWasteEmission);
        result.setTotal(energyEmission + transportEmission + solidWasteEmission);

        return result;
    }

    // Método que calcula a energia
    private double calculateEnergyEmission(Calculation calculation){
        if(calculation.getEnergyConsumption() ==  null) return 0.0;

        double factor = energyEmissionFactorRepository.findById(calculation.getUf())
                .map(EnergyEmissionFactor::getFactor)
                .orElse(0.0);
        return calculation.getEnergyConsumption() * factor;
    }

    // Método que o transporte
    private double calculateTransportationEmission(Calculation calculation){
        if(calculation.getTransportation() == null) return 0.0;

        return calculation.getTransportation().stream()
                .mapToDouble(t -> {
                    double factor = transportationEmissionFactorRepository.findById(t.getType())
                            .map(TransportationEmissionFactor::getFactor)
                            .orElse(0.0);
                    return t.getMonthlyDistance() * factor;
                })
                .sum();
    }

    // Método que calcula os resíduos sólidos
    private double calculateSolidWasteEmission(Calculation calculation){
        if(calculation.getSolidWasteTotal() == null) return 0.0;

        SolidWasteEmissionFactor factor = solidWasteEmissionFactorRepository.findAll()
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Solid waste factor not found"));

        double recyclable = calculation.getSolidWasteTotal() * calculation.getRecyclePercentage();
        double nonRecyclable = calculation.getSolidWasteTotal() * (1 - calculation.getRecyclePercentage());

        return recyclable * factor.getRecyclableFactor() + nonRecyclable * factor.getNonRecyclableFactor();
    }
}