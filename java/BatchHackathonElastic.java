package hackathon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import fr.insee.config.InseeConfig;
import fr.insee.sirene4liasse.batchS4.DemandeIdentificationProtoS4;
import fr.insee.sirene4liasse.batchS4.json.EtablissementElastic;
import fr.insee.sirene4liasse.batchS4.json.ReponseElasticJson;
import fr.insee.sirene4liasse.batchS4.json.ResultatJson;
import fr.insee.sirene4liasse.sirene4liasse.batch.domaine.entreprise.AdrSirene;
import fr.insee.sirene4liasse.sirene4liasse.batch.domaine.entreprise.geo.LocGeo;
import fr.insee.sirene4liasse.sirene4liasse.batch.service.identification.notation.NotationAdrResultat;
import fr.insee.sirene4liasse.sirene4liasse.batch.service.identification.notation.NotationAdrS3Service;
import fr.insee.sirene4liasse.sirene4liasse.batch.service.identification.notation.NotationEltDemande;
import fr.insee.sirene4liasse.sirene4liasse.batch.service.identification.notation.NotationEltVoisin;
import fr.insee.sirene4liasse.sirene4liasse.batch.service.identification.notation.NotationPMResultat;
import fr.insee.sirene4liasse.sirene4liasse.batch.service.identification.notation.NotationPMS3Service;
import fr.insee.sirene4liasse.sirene4liasse.batch.service.identification.notation.NotationS3Service;
import fr.insee.sirene4liasse.sirene4liasse.batch.transversal.HibernateUtil;

public class BatchHackathonElastic {

	Logger logger = Logger.getLogger(BatchHackathonElastic.class);

	private Configuration conf = InseeConfig.getConfig();

	public void exec() {
		logger.info("début du batch");
		Path path = Paths.get("src/main/resources/csv/rp_final_2017.csv900000.csv");
		Path path_o = Paths.get("src/main/resources/csv/output_elastic.csv");
		Path path_o_err = Paths.get("src/main/resources/csv/output_elastic_err.csv");
		String[] champs;
		DemandeIdentificationProtoS4 demandeIdentificationProtoS4;
		String json;
		ReponseElasticJson elasticJson;
		ResultatJson[] resultatJsons;
		EtablissementElastic etab;
		StringBuilder stringBuilder;
		
		try {
			BufferedReader reader = Files.newBufferedReader(path, Charset.forName("UTF-8"));
			BufferedWriter writer = Files.newBufferedWriter(path_o, Charset.forName("UTF-8"));
			BufferedWriter writer_err = Files.newBufferedWriter(path_o_err, Charset.forName("UTF-8"));
			reader.readLine();
			String currentLine = null;
			while ((currentLine = reader.readLine()) != null) {
				champs = currentLine.split(";");

				if (StringUtils.isNotEmpty(champs[1])) {

					 demandeIdentificationProtoS4 = new DemandeIdentificationProtoS4();
					demandeIdentificationProtoS4.setDesignation(champs[1]);
					if (StringUtils.isNotEmpty(champs[0]))
						demandeIdentificationProtoS4.setCabbi(champs[0]);
					if (StringUtils.isNotEmpty(champs[5]))
						demandeIdentificationProtoS4.setVoieLib(champs[5]);
					if (StringUtils.isNotEmpty(champs[6]))
						demandeIdentificationProtoS4.setVoieNumDeb(champs[6]);
					if (StringUtils.isNotEmpty(champs[7]))
						demandeIdentificationProtoS4.setVoieType(champs[7]);
					if (StringUtils.isNotEmpty(champs[10]))
						demandeIdentificationProtoS4.setComCode(champs[10]);// 14
																			// dans
																			// la
																			// théorie
					if (StringUtils.isNotEmpty(champs[3]))
						demandeIdentificationProtoS4.setApe(champs[3]);
					if (StringUtils.isNotEmpty(champs[11]))
						demandeIdentificationProtoS4.setComLib(champs[11]);
					/*if (StringUtils.isNotEmpty(champs[38])) {
						demandeIdentificationProtoS4.setSiren(champs[38].substring(0, 9));
						demandeIdentificationProtoS4.setNic(champs[38].substring(9, 14));
					}*/
					json = ServiceAdresseElastic.INSTANCE.execWithoutSSL(demandeIdentificationProtoS4);
					 
					 //System.out.println(json);
					elasticJson = ServiceAdresseElastic.INSTANCE.parseReponseElastic(json);

					if (elasticJson != null) {
						resultatJsons = elasticJson.getHit().getResultats();

						for (int i = 0; i < resultatJsons.length; i++) {
							etab = resultatJsons[i].getEtab();
							/*
							 * try{ notationSirene3(etab,
							 * demandeIdentificationProtoS4); }catch(Exception
							 * e){ etab.setNoteAdrDetail("0");
							 * etab.setNoteDenom("0"); etab.setNoteGlobale("0");
							 * etab.setNoteVoieLib("0"); }
							 */
							if (i == 0) {
								stringBuilder = new StringBuilder();
								// stringBuilder.append(currentLine);
								stringBuilder.append(
										demandeIdentificationProtoS4.getCabbi() + ";" + etab.getSiren() + etab.getNic()
												+ ";" + etab.getApet() + ";" + etab.getCj() + ";" + etab.getTrEffEtp());
								writer.newLine();
								writer.write(stringBuilder.toString());
								writer.flush();
								/*
								 * if (!(etab.getSiren() +
								 * etab.getNic()).equals(
								 * demandeIdentificationProtoS4.getSiren() +
								 * demandeIdentificationProtoS4.getNic())) {
								 * StringBuilder stringBuilder = new
								 * StringBuilder();
								 * stringBuilder.append(currentLine);
								 * stringBuilder.append(";" + etab.getSiren() +
								 * etab.getNic() + ";" + etab.getApet() + ";" +
								 * etab.getCj() + ";" + etab.getTrEffEtp());
								 * writer_err.newLine();
								 * writer_err.write(stringBuilder.toString());
								 * writer_err.flush(); }
								 */
							}

						}
					}

					/*
					 * Session session =
					 * HibernateUtil.getSessionFactory(conf.getString(
					 * "fr.insee.hibernate.connection.url")) .openSession(); try
					 * { session.beginTransaction(); session.save(reponseBan);
					 * session.getTransaction().commit();
					 * 
					 * } catch (Exception e) { logger.warn(e.getMessage());
					 * throw e; } finally { session.close(); }
					 */
				}
			}
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("fin du batch");

	}

	public void notationSirene3(EtablissementElastic etab, DemandeIdentificationProtoS4 demandeIdentificationProtoS4) {
		AdrSirene adrD = new AdrSirene();
		adrD.setLocGeo(new LocGeo());
		adrD.getLocGeo().setLocGeo(demandeIdentificationProtoS4.getComCode());
		adrD.setVoieLib(demandeIdentificationProtoS4.getVoieLib());
		adrD.setVoieNum(demandeIdentificationProtoS4.getVoieNumDeb());

		AdrSirene adrV = new AdrSirene();
		adrV.setVoieNum(etab.getAdrEtVoieNum());
		adrV.setVoieLib(etab.getAdrEtVoieLib());
		adrV.setLocGeo(new LocGeo());
		adrV.getLocGeo().setLocGeo(etab.getAdrDepcom());

		NotationEltDemande d = new NotationEltDemande();
		d.setLibellePM(demandeIdentificationProtoS4.getDesignation());
		d.setAdr(adrD);

		NotationEltVoisin v = new NotationEltVoisin();
		v.setDenom(etab.getDenom());
		AdrSirene[] adrs = new AdrSirene[1];
		adrs[0] = adrV;
		v.setAdrs(adrs);

		NotationAdrResultat adrResultat = NotationAdrS3Service.getInstance().execComparAdr(adrD, adrV);
		NotationPMResultat notationPMResultat = NotationPMS3Service.getInstance().exec(d, v);

		Integer noteGlobale = NotationS3Service.getInstance().calculNoteGlobale(false, adrResultat.getNoteAdrDetail(),
				notationPMResultat.getNoteDenom());

		etab.setNoteAdrDetail(String.valueOf(adrResultat.getNoteAdrDetail()));
		etab.setNoteDenom(String.valueOf(notationPMResultat.getNoteDenom()));
		etab.setNoteGlobale(String.valueOf(noteGlobale));
		etab.setNoteVoieLib(String.valueOf(adrResultat.getNoteVoieLib()));

		// System.out.println(etab.toString());
	}

	public static void main(String[] args) {
		BatchHackathonElastic batchHackathon = new BatchHackathonElastic();
		batchHackathon.exec();
	}

}
