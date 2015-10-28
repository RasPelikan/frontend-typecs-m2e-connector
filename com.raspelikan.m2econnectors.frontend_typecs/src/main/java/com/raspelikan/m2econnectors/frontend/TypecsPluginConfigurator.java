package com.raspelikan.m2econnectors.frontend;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IJavaProjectConfigurator;

import com.axmor.eclipse.typescript.builder.builder.TypescriptNature;
import com.axmor.eclipse.typescript.core.TypeScriptCompilerSettings;

public class TypecsPluginConfigurator extends AbstractProjectConfigurator
		implements IJavaProjectConfigurator {

	private static final Logger logger = Logger
			.getLogger(TypecsPluginConfigurator.class.getCanonicalName());

	public void configureClasspath(IMavenProjectFacade request,
			IClasspathDescriptor classpathDesc, IProgressMonitor monitor)
			throws CoreException {

		// nothing to do for typescript

	}

	public void configureRawClasspath(ProjectConfigurationRequest request,
			IClasspathDescriptor classpathDesc, IProgressMonitor monitor)
			throws CoreException {

		// nothing to do for typescript

	}

	@Override
	public void configure(ProjectConfigurationRequest request,
			IProgressMonitor monitor) throws CoreException {
		
		// add TypEcs's project-nature
		addNature(request.getProject(), TypescriptNature.NATURE_ID, monitor);
		
		// configure TypEcs-plugin
		setTypescriptCompilerSettings(request, monitor);

	}

	private void setTypescriptCompilerSettings(ProjectConfigurationRequest request,
			IProgressMonitor monitor) throws CoreException {

		// load current TypEcs settings
		TypeScriptCompilerSettings settings = TypeScriptCompilerSettings
				.load(request.getProject());

		final MavenProject project = request.getMavenProject();
		final IMavenProjectFacade mavenProjectFacade = request
				.getMavenProjectFacade();
		
		// process all executions this configuration is called for
		final List<MojoExecution> mojoExecutions = this.getMojoExecutions(request, monitor);
		for (final MojoExecution mojoExecution : mojoExecutions) {
			
			// read srcDir
			
			final String srcDir = maven.getMojoParameterValue(
					project, mojoExecution, "srcDir", String.class, monitor);
			
			final IPath projectRelativePathSrcDir = mavenProjectFacade
					.getProject().getFolder(srcDir).getProjectRelativePath();
			if (projectRelativePathSrcDir == null) {
				final Status status = new Status(
						IStatus.ERROR,
						"com.raspelikan.m2econnectors.frontend.TypecsPluginConfigurator",
						"The srcDir '" + srcDir + "' is not inside the Eclipse-project!");
				throw new CoreException(status);
			}
			
			settings.setSource(projectRelativePathSrcDir.toString());
			
			// read rootDir
			
			final String rootDir = maven.getMojoParameterValue(
					project, mojoExecution, "rootDir", String.class, monitor);
			if (rootDir != null) {
				
				final IPath projectRelativePathRootDir = mavenProjectFacade
						.getProject().getFolder(rootDir).getProjectRelativePath();
				if (projectRelativePathRootDir == null) {
					final Status status = new Status(
							IStatus.ERROR,
							"com.raspelikan.m2econnectors.frontend.TypecsPluginConfigurator",
							"The rootDir '" + rootDir + "' is not inside the Eclipse-project!");
					throw new CoreException(status);
				}
				
				if (!projectRelativePathSrcDir.equals(
						projectRelativePathRootDir)) {
					final Status status = new Status(
							IStatus.ERROR,
							"com.raspelikan.m2econnectors.frontend.TypecsPluginConfigurator",
							"The rootDir parameter is not equal to the srcDir. "
							+ "This configuration is not supported by TypEcs-plugin!");
					throw new CoreException(status);
				}
				
				settings.setTargetRelativePathBasedOnSource(true);
				
			} else {

				settings.setTargetRelativePathBasedOnSource(false);
				
			}
			
			// read outDir
			
			final String outDir = maven.getMojoParameterValue(
					project, mojoExecution, "outDir", String.class, monitor);
			
			logger.log(
					Level.WARNING,
					new Status(
							Status.INFO,
							"com.raspelikan.m2econnectors.frontend.TypecsPluginConfigurator",
							Status.OK, "outDir: '" + outDir + "'",
							null).toString());
			
			final IPath projectRelativePathOutDir = mavenProjectFacade
					.getProject().getFolder(outDir).getProjectRelativePath();
			if (projectRelativePathOutDir == null) {
				final Status status = new Status(
						IStatus.ERROR,
						"com.raspelikan.m2econnectors.frontend.TypecsPluginConfigurator",
						"The outDir '" + outDir + "' is not inside the Eclipse-project!");
				throw new CoreException(status);
			}
			
			settings.setTarget(projectRelativePathOutDir.toString());
			
		}
		
		settings.save();

	}

}
