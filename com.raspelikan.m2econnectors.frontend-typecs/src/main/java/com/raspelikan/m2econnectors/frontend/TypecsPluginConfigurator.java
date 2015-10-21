package com.raspelikan.m2econnectors.frontend;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IJavaProjectConfigurator;

public class TypecsPluginConfigurator extends AbstractProjectConfigurator
		implements IJavaProjectConfigurator {

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
		
		// add typescript-nature
		addTypescriptNature(monitor, project);		

	}

	private void addTypescriptNature(IProgressMonitor monitor,
			final IProject project) throws CoreException {
		
		IProjectDescription desc = project.getDescription();
	    String[] prevNatures = desc.getNatureIds();
	    
	    for (String prevNature : prevNatures) {
	    	if (prevNature.equals("com.axmor.eclipse.typescript.builder.typescriptNature")) {
	    		return;
	    	}
	    }
	    
	    String[] newNatures = new String[prevNatures.length + 1];
	    System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
	    newNatures[prevNatures.length] = "com.axmor.eclipse.typescript.builder.typescriptNature";
	    desc.setNatureIds(newNatures);
	    project.setDescription(desc, monitor);
	    
	}

}
