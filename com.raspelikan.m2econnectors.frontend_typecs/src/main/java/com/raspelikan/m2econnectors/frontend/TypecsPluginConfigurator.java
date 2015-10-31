package com.raspelikan.m2econnectors.frontend;

import java.util.List;
import java.util.logging.Logger;

import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
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

import com.axmor.eclipse.typescript.builder.builder.TypescriptBuilder;
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
		
		final IProject project = request.getProject();
		
		// add TypEcs's project-nature
		
		addNature(project, TypescriptNature.NATURE_ID, monitor);
		
		// configure TypEcs-plugin
		// load current TypEcs settings
		
		final boolean rebuild = setTypescriptCompilerSettings(
					request, monitor);

		// rebuild project if necessary
		
		if (rebuild) {
			
			logger.info("Rebuilding typescript-files...");
			
			project.build(IncrementalProjectBuilder.FULL_BUILD,
					TypescriptBuilder.BUILDER_ID, null, monitor);
			
		}

	}

	private boolean setTypescriptCompilerSettings(ProjectConfigurationRequest request,
			IProgressMonitor monitor) throws CoreException {

		final MavenProject project = request.getMavenProject();
		final IMavenProjectFacade mavenProjectFacade = request
				.getMavenProjectFacade();
		
		final TypeScriptCompilerSettings settings = TypeScriptCompilerSettings
				.load(request.getProject());
		
		boolean rebuild = false;
		
		// process all executions this configuration is called for
		final List<MojoExecution> mojoExecutions = this.getMojoExecutions(request, monitor);
		for (final MojoExecution mojoExecution : mojoExecutions) {
			
			// read srcDir
			
			String srcDir = maven.getMojoParameterValue(
					project, mojoExecution, "srcDir", String.class, monitor);
			
			final String projectPath = request.getProject().getLocation().toString();
			if (srcDir.startsWith(projectPath)) {
				srcDir = srcDir.substring(projectPath.length(), srcDir.length());
			}
			
			final IPath projectRelativePathSrcDir = mavenProjectFacade
					.getProject().getFolder(srcDir).getProjectRelativePath();
			if (projectRelativePathSrcDir == null) {
				final Status status = new Status(
						IStatus.ERROR,
						"com.raspelikan.m2econnectors.frontend.TypecsPluginConfigurator",
						"The srcDir '" + srcDir + "' is not inside the Eclipse-project!");
				throw new CoreException(status);
			}
			
			String srcDirRelative = projectRelativePathSrcDir.toString();
			if ((settings.getSource() == null)
					|| !settings.getSource().equals(srcDirRelative)) {
				rebuild = true;
			}
			settings.setSource(srcDirRelative);
			
			// read preserveDirectoryStructure
			
			final boolean preserveDirectoryStructure = maven.getMojoParameterValue(
					project, mojoExecution, "preserveDirectoryStructure", Boolean.class, monitor);
			if (settings.isTargetRelativePathBasedOnSource() != preserveDirectoryStructure) {
				rebuild = true;
			}
			settings.setTargetRelativePathBasedOnSource(preserveDirectoryStructure);
			
			// read outDir
			
			String outDir = maven.getMojoParameterValue(
					project, mojoExecution, "outDir", String.class, monitor);
			
			if (outDir.startsWith(projectPath)) {
				outDir = outDir.substring(projectPath.length(), outDir.length());
			}
			
			final IPath projectRelativePathOutDir = mavenProjectFacade
					.getProject().getFolder(outDir).getProjectRelativePath();
			if (projectRelativePathOutDir == null) {
				final Status status = new Status(
						IStatus.ERROR,
						"com.raspelikan.m2econnectors.frontend.TypecsPluginConfigurator",
						"The outDir '" + outDir + "' is not inside the Eclipse-project!");
				throw new CoreException(status);
			}
			
			String outDirRelative = projectRelativePathOutDir.toString();
			if ((settings.getTarget() == null)
					|| !settings.getTarget().equals(outDirRelative)) {
				rebuild = true;
			}
			settings.setTarget(outDir);
			
			// mapRoot
			
			final String mapRoot = maven.getMojoParameterValue(
					project, mojoExecution, "mapRoot", String.class, monitor);
			if (mapRoot != null) {
				if ((settings.getMapRoot() == null)
						|| !settings.getMapRoot().equals(mapRoot)) {
					rebuild = true;
				}
				settings.setMapRoot(mapRoot);
			} else {
				if (!settings.getMapRoot().equals("")) {
					rebuild = true;
				}
				settings.setMapRoot("");
			}
			
			// noResolve
			
			final boolean noResolve = maven.getMojoParameterValue(
					project, mojoExecution, "noResolve", Boolean.class, monitor);
			if (noResolve != settings.isNoResolve()) {
				rebuild = true;
			}
			settings.setNoResolve(noResolve);
			
			// noImplicitAny

			final boolean noImplicitAny = maven.getMojoParameterValue(
					project, mojoExecution, "noImplicitAny", Boolean.class, monitor);
			if (noImplicitAny != settings.isNoImplicitAny()) {
				rebuild = true;
			}
			settings.setNoImplicitAny(noImplicitAny);
			
			// declaration
			
			final boolean declaration = maven.getMojoParameterValue(
					project, mojoExecution, "declaration", Boolean.class, monitor);
			if (declaration != settings.isGenerateDeclaration()) {
				rebuild = true;
			}
			settings.setGenerateDeclaration(declaration);
			
			// sourceMap
			
			final boolean sourceMap = maven.getMojoParameterValue(
					project, mojoExecution, "sourceMap", Boolean.class, monitor);
			if (sourceMap != settings.isSourceMap()) {
				rebuild = true;
			}
			settings.setSourceMap(sourceMap);
			
			// removeComments
			
			final boolean removeComments = maven.getMojoParameterValue(
					project, mojoExecution, "removeComments", Boolean.class, monitor);
			if (removeComments != settings.isRemoveComments()) {
				rebuild = true;
			}
			settings.setRemoveComments(removeComments);
			
			// module
			
			final String module = maven.getMojoParameterValue(
					project, mojoExecution, "module", String.class, monitor);
			if (module != null) {
				if ((settings.getModule() == null)
						|| !module.equals(settings.getModule())) {
					rebuild = true;
				}
				settings.setModule(module);
			} else {
				if ((settings.getModule() == null)
						|| !"ES3".equals(settings.getModule())) {
					rebuild = true;
				}
				settings.setModule("ES3");
			}
			
			// target

			final String target = maven.getMojoParameterValue(
					project, mojoExecution, "target", String.class, monitor);
			if (target != null) {
				if ((settings.getTargetVersion() == null)
						|| !target.equals(settings.getTargetVersion())) {
					rebuild = true;
				}
				settings.setTargetVersion(target);
			} else {
				if ((settings.getTargetVersion() == null)
						|| !"default".equals(settings.getTargetVersion())) {
					rebuild = true;
				}
				settings.setTargetVersion("default");
			}
			
			settings.save();
			
		}
		
		return rebuild;

	}

}
