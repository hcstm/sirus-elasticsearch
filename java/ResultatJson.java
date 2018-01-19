package fr.insee.sirene4liasse.batchS4.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
Pojo ES
*/
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultatJson {

	@JsonProperty("_source")
	private EtablissementElastic etab;

	@JsonProperty("_score")
	private String score;

	public EtablissementElastic getEtab() {
		return etab;
	}

	public void setEtab(EtablissementElastic etab) {
		this.etab = etab;

	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

}
