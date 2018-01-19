package fr.insee.sirene4liasse.batchS4.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**Pojo ES
*/

@JsonIgnoreProperties(ignoreUnknown=true)
public class ReponseElasticJson {
	@JsonProperty("hits")
	private HitElastic hit;

	public HitElastic getHit() {
		return hit;
	}

	public void setHit(HitElastic hit) {
		this.hit = hit;
	}

	
	
	
	
	

}
