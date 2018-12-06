import com.rbkmoney.midgard.utils.XmlUtil;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.junit.Test;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.StringWriter;

public class MtsXmlUtilTest {


    @Test
    public void createTransactionXmlTest() throws ParserConfigurationException, TransformerException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        Element root = document.createElement("root");
        document.appendChild(root);

        System.out.println(XmlUtil.toXML(document));

    }

    @Test
    public void testVelocity() {
        Test1 test = new Test1();
//        Map<String, Object> velocityPropertiesMap = new HashMap<String, Object>();
//        velocityPropertiesMap.put("resource.loader", "class");
//        velocityPropertiesMap.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
//        vefb.setVelocityPropertiesMap(velocityPropertiesMap);
//        velocityEngine = vefb.createVelocityEngine();
        DateTool tool = new DateTool();

        VelocityEngine ve = new VelocityEngine();
        ve.setProperty("resource.loader", "class");
        ve.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        ve.init();

        Template t = ve.getTemplate("vm/transactionMtsVersion.vm");
        VelocityContext context = new VelocityContext();
        context.put("test", test);
        StringWriter w = new StringWriter();
        t.merge(context, w);
        System.out.println("-> " + w.toString());
    }
    //    VelocityEngine ve = new VelocityEngine();
    //    ve.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, "./src/test/resources/");
    //    ve.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_CACHE, false);
    //    ve.init();

    public class Test1 {
        public Test1() {}

        public String getValue() { return "strrrrrrrr"; }
    }


}
