package fr.insee.sirene4liasse.batchS4.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**Pojo ES
*/
@JsonIgnoreProperties(ignoreUnknown=true)
public class EtablissementElastic {

	@JsonProperty("adr_et_loc_geo")
	private String adrEtLocGeo;
	
	@JsonProperty("adr_depcom")
	private String adrDepcom;
	
	@JsonProperty("adr_et_voie_num")
	private String adrEtVoieNum;
	
	@JsonProperty("adr_et_voie_lib")
	private String adrEtVoieLib;
	
	@JsonProperty("sirus_id")
	private String siren;
	
	@JsonProperty("nic")
	private String nic;
	
	@JsonProperty("adr_et_voie_repet")
	private String adrEtVoieRepet;
	
	@JsonProperty("adr_et_voie_type")
	private String adrEtVoieType;
	
	@JsonProperty("apet")
	private String apet;
	
	@JsonProperty("denom")
	private String denom;
	
	@JsonProperty("tr_eff_etp")
	private String trEffEtp;
	
	@JsonProperty("cj")
	private String cj;
	
	private String noteAdrDetail;
	private String noteVoieLib;
	private String noteGlobale;
	private String noteDenom;
	
	public String getAdrEtLocGeo() {
		return adrEtLocGeo;
	}

	public void setAdrEtLocGeo(String adrEtLocGeo) {
		this.adrEtLocGeo = adrEtLocGeo;
	}

	public String getAdrDepcom() {
		return adrDepcom;
	}

	public void setAdrDepcom(String adrDepcom) {
		this.adrDepcom = adrDepcom;
	}

	public String getAdrEtVoieNum() {
		return adrEtVoieNum;
	}

	public void setAdrEtVoieNum(String adrEtVoieNum) {
		this.adrEtVoieNum = adrEtVoieNum;
	}

	public String getAdrEtVoieLib() {
		return adrEtVoieLib;
	}

	public void setAdrEtVoieLib(String adrEtVoieLib) {
		this.adrEtVoieLib = adrEtVoieLib;
	}

	public String getSiren() {
		return siren;
	}

	public void setSiren(String siren) {
		this.siren = siren;
	}

	public String getNic() {
		return nic;
	}

	public void setNic(String nic) {
		this.nic = nic;
	}

	public String getAdrEtVoieRepet() {
		return adrEtVoieRepet;
	}

	public void setAdrEtVoieRepet(String adrEtVoieRepet) {
		this.adrEtVoieRepet = adrEtVoieRepet;
	}

	public String getAdrEtVoieType() {
		return adrEtVoieType;
	}

	public void setAdrEtVoieType(String adrEtVoieType) {
		this.adrEtVoieType = adrEtVoieType;
	}

	public String getApet() {
		return apet;
	}

	public void setApet(String apet) {
		this.apet = apet;
	}

	public String getDenom() {
		return denom;
	}

	public void setDenom(String denom) {
		this.denom = denom;
	}

	public String getNoteAdrDetail() {
		return noteAdrDetail;
	}

	public void setNoteAdrDetail(String noteAdrDetail) {
		this.noteAdrDetail = noteAdrDetail;
	}

	public String getNoteVoieLib() {
		return noteVoieLib;
	}

	public void setNoteVoieLib(String noteVoieLib) {
		this.noteVoieLib = noteVoieLib;
	}

	public String getNoteGlobale() {
		return noteGlobale;
	}

	public void setNoteGlobale(String noteGlobale) {
		this.noteGlobale = noteGlobale;
	}

	public String getNoteDenom() {
		return noteDenom;
	}

	public void setNoteDenom(String noteDenom) {
		this.noteDenom = noteDenom;
	}

	public String getTrEffEtp() {
		return trEffEtp;
	}

	public void setTrEffEtp(String trEffEtp) {
		this.trEffEtp = trEffEtp;
	}

	public String getCj() {
		return cj;
	}

	public void setCj(String cj) {
		this.cj = cj;
	}
	
	
}
