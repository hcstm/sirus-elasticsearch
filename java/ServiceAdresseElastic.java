package hackathon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;


import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.client.protocol.RequestClientConnControl;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.client.JerseyInvocation.Builder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.insee.config.InseeConfig;
import fr.insee.sirene4liasse.batchS4.DemandeIdentificationProtoS4;
import fr.insee.sirene4liasse.batchS4.ReponseIdentificationSolR;
import fr.insee.sirene4liasse.batchS4.json.ReponseElasticJson;
import fr.insee.sirene4liasse.sirene4liasse.batch.domaine.entreprise.AdrSirene;
import fr.insee.sirene4liasse.sirene4liasse.batch.service.identification.notation.NotationAdrS3Service;

public enum ServiceAdresseElastic {
	INSTANCE;

	private Configuration conf = InseeConfig.getConfig();
	
		
	public String execWithoutSSL(DemandeIdentificationProtoS4 demandeIdentification)
			throws UnsupportedEncodingException {
		//System.setProperty("http.proxyHost", "proxy-rie.http.insee.fr");
		//System.setProperty("http.proxyPort", "8080");
		String json = null;
		String dep = null;
		//String URL = "http//elastic-bguq8j.hackathon.insee.eu/sirus_basic_mapping/_search/";
		//String URL = "http://api-adresse.data.gouv.fr/search/";
		//String URL = "http://localhost:8080/formations";
//		String finUrl = "/sirus_ngrammes/_search";
		String finUrl = "/sirus_basic_mapping/_search/template";
		Header[] headers = { new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"),
                new BasicHeader("Role", "Read") };
		RestClient restClient = RestClient.builder(new HttpHost("elastic-2017.hackathon.insee.eu", 80, "http" )).setDefaultHeaders(headers).build();
		Response response;
		StringBuilder localisation = new StringBuilder();
		if(demandeIdentification.getVoieNumDeb()!=null) localisation.append(" "+demandeIdentification.getVoieNumDeb());
		if(demandeIdentification.getVoieNumIndRepet()!=null) localisation.append(" "+ demandeIdentification.getVoieNumIndRepet());
		if(demandeIdentification.getVoieLib()!=null) localisation.append(" "+demandeIdentification.getVoieLib());
		if(demandeIdentification.getComLib()!=null) localisation.append(" "+ demandeIdentification.getComLib());
		if(demandeIdentification.getComCode()!=null){
			localisation.append(" "+demandeIdentification.getComCode());
			dep = demandeIdentification.getComCode().substring(0, 2);
		}
		
		
		try {
			/*HttpEntity entity = new NStringEntity(jsonRequestDoubleApet(demandeIdentification.getDesignation(), localisation.toString().trim(), demandeIdentification.getApe())
					, ContentType.APPLICATION_JSON);*/
			HttpEntity entity = new NStringEntity(jsonRequestBestMAtch(demandeIdentification.getDesignation(), localisation.toString().trim(), demandeIdentification.getApe(), dep)
					, ContentType.APPLICATION_JSON);
			response = restClient.performRequest("GET", finUrl,
					 Collections.<String, String>emptyMap(),
				        entity);
			json = EntityUtils.toString(response.getEntity());
			//System.out.println(json);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

			
		return json;
	}
	
	public ReponseElasticJson parseReponseElastic(String json) {
		ObjectMapper mapper = new ObjectMapper();
		ReponseElasticJson reponse = null;
		try {
			reponse = mapper.readValue(json, ReponseElasticJson.class);
		} catch (IOException e) {
			System.out.println("erreur parsing :"+json);
		}
		return reponse;
	}
	public String jsonRequest(String description, String localisation){
		String jsonRequest ="{\n"+
				" \"query\": {\"match\" : { \n" +
					" \"description\" : \""+ description+"\" \n"+
					"} \n" +
					"} \n" +
					"}";
		//System.out.println(jsonRequest);
		return jsonRequest;
	}
	public String jsonRequestDouble(String description, String localisation){
		String jsonRequest ="{\n"+
				"\"from\" : 0, \"size\" : 1,\n"+
				" \"query\": {\n"+
				"\"bool\" : {\n"+
				"\"should\" : [\n"+
		        "{ \"match\" : { \"description.ngrammes\" : \""+description+"\" } },\n"+
		        "{ \"match\" : { \"localisation.ngrammes\" : \""+localisation+"\" } }\n"+
		        "], \n"+
		        " \"minimum_should_match\" : 1,\n"+
		        " \"boost\" : 1.0 \n"+
		    	"}\n"+
		  		"} \n" +
					"}";
		//System.out.println(jsonRequest);
		return jsonRequest;
	}
	public String jsonRequestDoubleApet(String description, String localisation, String apet){
		String jsonRequest ="{\n"+
				"\"from\" : 0, \"size\" : 1,\n"+
				" \"query\": {\n"+
				"\"bool\" : {\n"+
				"\"should\" : [\n"+
		        "{ \"match\" : { \"description.ngrammes\" : \""+description+"\" } },\n"+
		        "{ \"match\" : { \"localisation.ngrammes\" : \""+localisation+"\" } },\n"+
		        "{ \"term\" : { \"apet\" : \""+ apet + "\" } }\n"+
		    	"], \n"+
		        " \"minimum_should_match\" : 1,\n"+
		        " \"boost\" : 1.0 \n"+
		    	"}\n"+
		  		"} \n" +
					"}";
		//System.out.println(jsonRequest);
		return jsonRequest;
	}
/*	public String jsonRequestBestMAtch(String description, String localisation, String apet, String dep){
		String jsonRequest ="{\n" +
				" \"query\":{\n"+
				"\"bool\":{\n"+
				"\"should\":[{\n"+
				"\"multi_match\":{\"query\":\"{{localisation}}\",\"type\":\"best_fields\",\"fields\":[\"localisation\",\"sir_adr_et_com_lib^10\",\"adr_et_l6^10\",\"adr_et_voie_lib^10\",\"adr_et_l4^10\"],\"boost\":4}},{\"multi_match\":{\"query\":\"{{description}}\",\"type\":\"best_fields\",\"fields\":[\"description\",\"enseigne^10\",\"denom^10\",\"denom_condense^10\",\"enseigne_et1^10\",\"nom_comm_et^10\",\"adr_et_l1^10\",\"adr_et_l2^10\"],\"boost\":4}},{\"prefix\":{\"adr_et_loc_geo\":{\"value\":\"{{departement}}\",\"boost\":8.0}}},{\"fuzzy\":{\"denom\":{\"value\":\"{{description}}\",\"prefix_length\":4,\"boost\":2.0}}},{\"fuzzy\":{\"adr_et_loc_geo\":{\"value\":\"{{localisation}}\",\"prefix_length\":5,\"boost\":2.0}}}],"+
				"\"minimum_should_match\":1}}\n" +
				" },\n" +
				" \"params\" : {\n" +
				" \"localisation\" : \""+localisation+"\",\n" +
				" \"description\" : \""+description+"\",\n" +
				" \"apet\" : \""+apet+"\",\n" +
				" \"departement\" : \""+dep+"\"\n" +
				" }\n" +
				"}\n";
		System.out.println(jsonRequest);
		return jsonRequest;
	}*/
	public String jsonRequestBestMAtch(String description, String localisation, String apet, String dep){
		String jsonRequest ="{\n"+
				"\"source\" : {\n"+
				"\"query\":{\"bool\":{\"should\":[{\"multi_match\":{\"query\":\"{{localisation}}\",\"type\":\"best_fields\",\"fields\":[\"localisation\",\"sir_adr_et_com_lib^10\",\"adr_et_l6^10\",\"adr_et_voie_lib^10\",\"adr_et_l4^10\"],\"boost\":4}},{\"multi_match\":{\"query\":\"{{description}}\",\"type\":\"best_fields\",\"fields\":[\"description\",\"enseigne^10\",\"denom^10\",\"denom_condense^10\",\"enseigne_et1^10\",\"nom_comm_et^10\",\"adr_et_l1^10\",\"adr_et_l2^10\"],\"boost\":4}},{\"prefix\":{\"apet\":{\"value\":\"{{apet}}\",\"boost\":2.0}}},{\"prefix\":{\"adr_et_loc_geo\":{\"value\":\"{{departement}}\",\"boost\":8.0}}},{\"fuzzy\":{\"denom\":{\"value\":\"{{description}}\",\"prefix_length\":4,\"boost\":2.0}}},{\"fuzzy\":{\"adr_et_loc_geo\":{\"value\":\"{{localisation}}\",\"prefix_length\":5,\"boost\":2.0}}}],\"minimum_should_match\":1}}\n"+
				"},\n"+
				"\"params\" : {\n"+
				"\"localisation\" : \""+localisation+"\",\n"+
				"\"description\" : \""+description+"\",\n"+
				"\"apet\" : \""+apet+"\",\n"+
				"\"departement\" : \""+dep+"\"\n"+
"}\n"+
"}";
		System.out.println(jsonRequest);
		return jsonRequest;
	}
	
	public String execMimine() throws MalformedURLException, IOException{
		String URL = "http://devapi-adresse.data.gouv.fr/search?q=39%20quai%20andre%20citroen";
		InputStream stream = new URL(URL).openConnection().getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String ligne;
		while((ligne = reader.readLine())!=null){
			System.out.println(ligne);
		}
		return null;
	}
}
