package org.apache.maven.plugin.my;

import fr.pantheonsorbonne.ufr27.MyMojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.project.MavenProject;

import java.io.File;

public class MyMojoTest
        extends AbstractMojoTestCase
{
    /** {@inheritDoc} */
    protected void setUp()
            throws Exception
    {
        // required
        super.setUp();


    }

    /** {@inheritDoc} */
    protected void tearDown()
            throws Exception
    {
        // required
        super.tearDown();


    }



    /**
     * @throws Exception if any
     */
    public void testSomething()
            throws Exception
    {
        File pom = getTestFile( "src/test/resources/unit/project-to-test/pom.xml" );
        assertNotNull( pom );
        assertTrue( pom.exists() );

        MyMojo myMojo = (MyMojo) lookupMojo( "touch", pom );
        assertNotNull( myMojo );
        myMojo.execute();


    }
}
