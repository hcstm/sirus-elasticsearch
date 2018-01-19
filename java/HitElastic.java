package fr.insee.sirene4liasse.batchS4.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
/**Pojo ES
*/
@JsonIgnoreProperties(ignoreUnknown=true)
public class HitElastic {
	
	@JsonProperty("hits")
	private ResultatJson[] resultats;

	public ResultatJson[] getResultats() {
		return resultats;
	}

	public void setResultats(ResultatJson[] resultats) {
		this.resultats = resultats;
	}
	

}
