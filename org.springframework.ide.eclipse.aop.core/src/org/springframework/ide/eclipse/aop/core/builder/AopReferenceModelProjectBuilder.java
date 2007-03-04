/*
 * Copyright 2002-2007 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.ide.eclipse.aop.core.builder;

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.springframework.ide.eclipse.aop.core.model.builder.AopReferenceModelBuilder;
import org.springframework.ide.eclipse.aop.core.util.AopReferenceModelUtils;
import org.springframework.ide.eclipse.core.project.IProjectBuilder;

/**
 * A {@link IProjectBuilder} implementation that triggers creation of Spring
 * IDE's internal AOP reference model
 * 
 * @author Christian Dupuis
 * @since 2.0
 */
@SuppressWarnings("restriction")
public class AopReferenceModelProjectBuilder implements IProjectBuilder {

	/**
	 * @see IProjectBuilder#build(IFile, IProgressMonitor)
	 */
	public void build(IFile file, IProgressMonitor monitor) {
		Set<IFile> filesToBuild = AopReferenceModelUtils.getFilesToBuild(file);
		monitor.beginTask("Building Spring AOP reference model", filesToBuild.size());
		AopReferenceModelBuilder.buildAopModel(file.getProject(), filesToBuild);
		monitor.done();
	}
}
