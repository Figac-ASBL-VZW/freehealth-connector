package org.taktik.connector.business.kmehrcommons;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDADDRESSschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDCOUNTRYschemes;
import org.taktik.connector.business.kmehrcommons.builders.Cd;
import org.taktik.connector.business.kmehrcommons.builders.HcPartyBuilder;
import org.taktik.connector.business.kmehrcommons.builders.Id;
import org.taktik.connector.business.kmehrcommons.util.KmehrIdGenerator;
import org.taktik.connector.technical.config.ConfigFactory;
import org.taktik.connector.technical.config.Configuration;
import org.taktik.connector.technical.exception.TechnicalConnectorException;
import org.taktik.connector.technical.exception.TechnicalConnectorExceptionValues;
import org.taktik.connector.technical.idgenerator.IdGeneratorFactory;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTY;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTYschemes;
import be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTY;
import be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTYschemes;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes;
import be.fgov.ehealth.standards.kmehr.schema.v1.AuthorType;
import be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.taktik.connector.technical.utils.IdentifierType;
import org.taktik.connector.technical.utils.SessionUtil;

public final class HcPartyUtil {
   private static final Logger LOG = LoggerFactory.getLogger(HcPartyUtil.class);
   private static final String MAIN_KMEHR_QUALITY = "main.kmehr.quality";
   private static final String AUTHOR_PREFIX = "kmehr.";
   private static final String SINGLE_HCPARTY_CONFIG_PREFIX = "kmehr.single.hcparty.template.";
   private static final String MULTIPLE_HCPARTIES_CONFIG_PREFIX = "kmehr.multiple.hcparties.template.";
   private static final String IDVERSION = "1.0";

   public static String getAuthorKmehrQuality() {
      String quality = ConfigFactory.getConfigValidator().getProperty("main.kmehr.quality");
      if (quality != null && !quality.isEmpty()) {
         return quality;
      } else {
         throw new IllegalStateException("configuration did not contain required parameter main.kmehr.quality");
      }
   }

   public static List<HcpartyType> createHcpartiesFromConfig(String configName) throws TechnicalConnectorException {
      String prefix = "kmehr.multiple.hcparties.template." + configName + ".";
      LOG.debug("createHcpartiesFromConfig for prefix " + prefix);
      return buildHcpartiesFromConfig(prefix);
   }

   public static HcpartyType createSingleHcpartyFromConfig(String configName) throws TechnicalConnectorException {
      String prefix = "kmehr.single.hcparty.template." + configName;
      LOG.debug("createSingleHcpartyFromConfig for prefix " + prefix);
      return buildHcpartyFromConfig(prefix);
   }

   public static HcpartyType createSoftwareIdentifier(String softwareName, String appId) throws TechnicalConnectorException {
      HcPartyBuilder builder = new HcPartyBuilder();
      builder.cd("1.1", "application", CDHCPARTYschemes.CD_HCPARTY);
      builder.id("1.0", appId, IDHCPARTYschemes.LOCAL, "application_ID");
      builder.name(softwareName);
      return builder.build();
   }

   public static HcpartyType createProfessionalParty(String inss, String nihii, String professionType) throws TechnicalConnectorException {
      HcPartyBuilder builder = new HcPartyBuilder();
      builder.cd("1.1", professionType, CDHCPARTYschemes.CD_HCPARTY);
      builder.id("1.0", inss, IDHCPARTYschemes.INSS);
      builder.id("1.0", nihii, IDHCPARTYschemes.ID_HCPARTY);
      return builder.build();
   }

   private static HcpartyType buildHcpartyFromConfig(String hcpartyPropertyPrefix) throws TechnicalConnectorException {
      HcPartyBuilder builder = new HcPartyBuilder();
      Configuration config = ConfigFactory.getConfigValidator();
      buildLocal(config, builder, hcpartyPropertyPrefix + ".id");
      buildLocal(config, builder, hcpartyPropertyPrefix + ".cd");
      String idHcPartyValue = config.getProperty(hcpartyPropertyPrefix + ".id.idhcparty.value");
      if (idHcPartyValue != null) {
         builder.idHcPartyId(idHcPartyValue, config.getProperty(hcpartyPropertyPrefix + ".id.idhcparty.sv"));
      }

      String inssIdValue = config.getProperty(hcpartyPropertyPrefix + ".id.inss.value");
      if (inssIdValue != null) {
         builder.inssId(inssIdValue, config.getProperty(hcpartyPropertyPrefix + ".id.inss.sv"));
      }

      String cdHcPartyValue = config.getProperty(hcpartyPropertyPrefix + ".cd.cdhcparty.value");
      if (cdHcPartyValue != null) {
         builder.cdHcPartyCd(cdHcPartyValue, config.getProperty(hcpartyPropertyPrefix + ".cd.cdhcparty.sv"));
      }

      String nameValue = config.getProperty(hcpartyPropertyPrefix + ".name");
      if (nameValue != null) {
         builder.name(nameValue);
      }

      String firstnameValue = config.getProperty(hcpartyPropertyPrefix + ".firstname");
      if (firstnameValue != null) {
         builder.firstname(firstnameValue);
      }

      String lastnameValue = config.getProperty(hcpartyPropertyPrefix + ".lastname");
      if (lastnameValue != null) {
         builder.lastname(lastnameValue);
      }

	   String addressCdValue = config.getProperty(hcpartyPropertyPrefix + ".address.cd.value");
	   if (addressCdValue != null) {
		   String addressCdType = config.getProperty(hcpartyPropertyPrefix + ".address.cd.schemes");
		   String countryType = config.getProperty(hcpartyPropertyPrefix + ".address.country.schemes");
		   CDADDRESSschemes addressSchemes = null;
		   CDCOUNTRYschemes countrySchemes = null;
		   if (addressCdType != null) {
			   if (addressCdType.equals("local")) {
				   addressSchemes = CDADDRESSschemes.LOCAL;
			   } else {
				   addressSchemes = CDADDRESSschemes.CD_ADDRESS;
			   }
		   }

		   if (countryType != null) {
			   if (countryType.equals("cdcountry")) {
				   countrySchemes = CDCOUNTRYschemes.CD_COUNTRY;
			   } else {
				   countrySchemes = CDCOUNTRYschemes.CD_FED_COUNTRY;
			   }
		   }

		   builder.address(addressCdValue, addressSchemes, config.getProperty(hcpartyPropertyPrefix + ".address.cd.sv"), config.getProperty(hcpartyPropertyPrefix + ".address.city"), config.getProperty(hcpartyPropertyPrefix + ".address.district"), config.getProperty(hcpartyPropertyPrefix + ".address.houseNumber"), config.getProperty(hcpartyPropertyPrefix + ".address.nis"), config.getProperty(hcpartyPropertyPrefix + ".address.postbox"), config.getProperty(hcpartyPropertyPrefix + ".address.street"), config.getProperty(hcpartyPropertyPrefix + ".address.zip"), config.getProperty(hcpartyPropertyPrefix + ".address.country.value"), countrySchemes, config.getProperty(hcpartyPropertyPrefix + ".address.country.sv"));
	   }

	   return builder.build();
   }

   private static void buildLocal(Configuration config, HcPartyBuilder builder, String propertyConcerned) throws TechnicalConnectorException {
      String idSearchedPropertyValue = propertyConcerned + ".local.value";
      String idSearchedPropertySv = propertyConcerned + ".local.sv";
      String idSearchedPropertysl = propertyConcerned + ".local.sl";
      String elementType = StringUtils.substringAfterLast(propertyConcerned, ".");
      List<String> keys = config.getMatchingProperties(idSearchedPropertysl);
      List<String> keys2 = config.getMatchingProperties(idSearchedPropertyValue);
      if (keys2.size() != keys.size()) {
         throw new TechnicalConnectorException(TechnicalConnectorExceptionValues.ERROR_CONFIG, new Object[]{elementType + " local : number of type not equal of number of value"});
      } else {
         for(int i = 1; i <= keys.size(); ++i) {
            String localIdValue = config.getProperty(idSearchedPropertyValue + "." + i);
            if (localIdValue != null) {
               builder.localId(localIdValue, config.getProperty(idSearchedPropertySv + "." + i), config.getProperty(idSearchedPropertysl + "." + i));
            }
         }

      }
   }

   private static List<HcpartyType> buildHcpartiesFromConfig(String configPrefix) throws TechnicalConnectorException {
      String hcPartylist = ConfigFactory.getConfigValidator().getProperty(configPrefix + "hcpartylist");
      if (hcPartylist == null) {
         throw new TechnicalConnectorException(TechnicalConnectorExceptionValues.ERROR_CONFIG, new Object[]{configPrefix + "hcpartylist property not found"});
      } else {
         List<HcpartyType> kmehrlist = new ArrayList();
         List<String> elements = Arrays.asList(hcPartylist.split(","));
         Iterator i$ = elements.iterator();

         while(i$.hasNext()) {
            String element = (String)i$.next();
            kmehrlist.add(buildHcpartyFromConfig(configPrefix + element));
         }

         return kmehrlist;
      }
   }


   /** @deprecated */
   @Deprecated
   public static String createKmehrIdPrefix() throws TechnicalConnectorException {
      return IdGeneratorFactory.getIdGenerator("kmehr").generateId();
   }

   public static String createKmehrIdSuffix() throws TechnicalConnectorException {
      return IdGeneratorFactory.getIdGenerator("kmehr").generateId();
   }
   
   public static IDHCPARTY createInssId(String insz) {
      return buildId("1.0", insz, IDHCPARTYschemes.INSS);
   }

   public static IDHCPARTY createNihiiId(String nihii) {
      return buildId("1.0", nihii, IDHCPARTYschemes.ID_HCPARTY);
   }

   public static IDHCPARTY createCbeId(String cbe) {
      return buildId("1.0", cbe, IDHCPARTYschemes.ID_HCPARTY);
   }

   public static IDHCPARTY createApplicationId(String applicationId) {
      return buildId("1.0", applicationId, IDHCPARTYschemes.LOCAL, "application_ID");
   }

   public static IDHCPARTY createEhpId(String ehpId) {
      return buildId("1.0", ehpId, IDHCPARTYschemes.ID_HCPARTY);
   }

   public static IDHCPARTY buildId(String version, String value, IDHCPARTYschemes scheme, String sl) {
      IDHCPARTY id = new IDHCPARTY();
      id.setValue(value);
      id.setSV(version);
      if (sl != null) {
         id.setSL(sl);
      }

      id.setS(scheme);
      return id;
   }

   public static IDHCPARTY buildId(String version, String value, IDHCPARTYschemes scheme) {
      IDHCPARTY id = new IDHCPARTY();
      id.setValue(value);
      id.setSV(version);
      id.setS(scheme);
      return id;
   }

   public static IDHCPARTY buildId(String value, IDHCPARTYschemes scheme) {
      if (scheme == null) {
         throw new IllegalArgumentException("HcPartyUtil.buildId : parameter scheme was null");
      } else {
         String version = ConfigFactory.getConfigValidator().getProperty("kmehr.builder.idhcparty." + scheme.name() + ".version", "1.0");
         return buildId(version, value, scheme);
      }
   }

   public static CDHCPARTY buildCd(String sv, String value, CDHCPARTYschemes scheme, String sl) {
      CDHCPARTY cd = new CDHCPARTY();
      cd.setS(scheme);
      cd.setSV(sv);
      cd.setSL(sl);
      cd.setValue(value);
      return cd;
   }

   public static List<HcpartyType> createAuthorHcParties(String projectName) throws TechnicalConnectorException {
      String finalProjectName = determineProjectNameToUse(projectName);
      return buildHcpartiesFromConfig("kmehr." + finalProjectName + ".");
   }

	private static String determineProjectNameToUse(String projectName) throws TechnicalConnectorException {
      Configuration config = ConfigFactory.getConfigValidator().getConfig();
      String finalProjectName = projectName;
      if (config.getBooleanProperty("kmehr." + projectName + ".usedefaultproperties", true).booleanValue()) {
         finalProjectName = "default";
      }

      return finalProjectName;
   }

   static {
      IdGeneratorFactory.registerDefaultImplementation("kmehr", KmehrIdGenerator.class);
   }
}
