package com.raspelikan.m2econnectors.frontend;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IJavaProjectConfigurator;

public class TypecsPluginConfigurator extends AbstractProjectConfigurator
		implements IJavaProjectConfigurator {

	private static final String BUNDLE_ID = "com.raspelikan.m2econnectors.frontend-typecs";

	public void configureClasspath(IMavenProjectFacade arg0,
			IClasspathDescriptor arg1, IProgressMonitor arg2)
			throws CoreException {
		// TODO Auto-generated method stub

	}

	public void configureRawClasspath(ProjectConfigurationRequest arg0,
			IClasspathDescriptor arg1, IProgressMonitor arg2)
			throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public void configure(ProjectConfigurationRequest arg0,
			IProgressMonitor arg1) throws CoreException {
		// TODO Auto-generated method stub

	}

}
