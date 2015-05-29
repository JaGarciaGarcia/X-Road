package ee.ria.xroad.confproxy.commandline;

import java.io.OutputStream;
import java.nio.file.AccessDeniedException;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.cli.CommandLine;

import ee.ria.xroad.common.conf.globalconf.ConfigurationAnchor;
import ee.ria.xroad.common.conf.globalconf.privateparameters.ConfigurationAnchorType;
import ee.ria.xroad.common.conf.globalconf.privateparameters.ConfigurationSourceType;
import ee.ria.xroad.common.conf.globalconf.privateparameters.ObjectFactory;
import ee.ria.xroad.common.util.AtomicSave;
import ee.ria.xroad.confproxy.ConfProxyProperties;
import ee.ria.xroad.confproxy.util.OutputBuilder;

/**
 * Utility tool for creating an anchor file that is used for downloading
 * the generated global configuration.
 */
public class ConfProxyUtilGenerateAnchor extends ConfProxyUtil {

    /**
     * Constructs a confproxy-generate-anchor utility program instance.
     */
    ConfProxyUtilGenerateAnchor() {
        super("confproxy-generate-anchor");
        getOptions()
            .addOption(PROXY_INSTANCE)
            .addOption("f", "filename", true,
                    "Filename of the generated anchor");
    }

    @Override
    final void execute(final CommandLine commandLine)
            throws Exception {
        ensureProxyExists(commandLine);
        final ConfProxyProperties conf = loadConf(commandLine);
        ConfigurationAnchor sourceAnchor =
                new ConfigurationAnchor(conf.getProxyAnchorPath());

        if (commandLine.hasOption("filename")) {
            String filename = commandLine.getOptionValue("f");

            try {
                AtomicSave.execute(filename, "tmpanchor",
                        out -> generateAnchorXml(conf,
                                sourceAnchor.getInstanceIdentifier(), out));
            } catch (AccessDeniedException ex) {
                throw new Exception("Cannot write anchor to '"
                        + filename + "', permission denied.");
            }
            System.out.println("Generated anchor xml to '" + filename + "'");
        } else {
            printHelp();
            System.exit(0);
        }
    }

    /**
     * Generates an achor xml file based on the provided proxy configuration
     * properties and writes it to the provided output stream.
     * @param conf configuration proxy properties instance
     * @param instanceIdentifier instance identifier of the resulting anchor
     * @param out the output stream for writing the generated xml
     * @throws Exception if xml generation fails
     */
    private void generateAnchorXml(final ConfProxyProperties conf,
            final String instanceIdentifier, final OutputStream out)
                    throws Exception {
        JAXBContext jaxbCtx = JAXBContext.newInstance(ObjectFactory.class);
        Marshaller marshaller = jaxbCtx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        ObjectFactory factory = new ObjectFactory();
        ConfigurationSourceType sourceType =
                factory.createConfigurationSourceType();
        sourceType.setDownloadURL(conf.getConfigurationProxyURL() + "/"
                + OutputBuilder.SIGNED_DIRECTORY_NAME);
        for (byte[] cert : conf.getVerificationCerts()) {
            sourceType.getVerificationCert().add(cert);
        }
        ConfigurationAnchorType anchorType =
                factory.createConfigurationAnchorType();
        anchorType.setInstanceIdentifier(instanceIdentifier);
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeZone(TimeZone.getTimeZone("UTC"));
        XMLGregorianCalendar xgcal = DatatypeFactory.newInstance()
              .newXMLGregorianCalendar(gcal);
        anchorType.setGeneratedAt(xgcal);
        anchorType.getSource().add(sourceType);
        JAXBElement<ConfigurationAnchorType> root =
                factory.createConfigurationAnchor(anchorType);

        marshaller.marshal(root, out);
    }
}