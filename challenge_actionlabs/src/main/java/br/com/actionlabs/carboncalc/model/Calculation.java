package br.com.actionlabs.carboncalc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("calculation")
public class Calculation {

    @Id
    private String id;

    private String name;
    private String email;
    private String phoneNumber;
    private String uf;

    private Integer energyConsumption;
    private List<Transportation> transportation;
    private Integer solidWasteTotal;
    private Double recyclePercentage;
}
