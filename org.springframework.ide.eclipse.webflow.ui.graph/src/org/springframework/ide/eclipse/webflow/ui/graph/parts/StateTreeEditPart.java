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
package org.springframework.ide.eclipse.webflow.ui.graph.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.springframework.ide.eclipse.webflow.core.model.IActionState;
import org.springframework.ide.eclipse.webflow.core.model.IAttributeEnabled;
import org.springframework.ide.eclipse.webflow.core.model.IDecisionState;
import org.springframework.ide.eclipse.webflow.core.model.IInlineFlowState;
import org.springframework.ide.eclipse.webflow.core.model.IState;
import org.springframework.ide.eclipse.webflow.core.model.ISubflowState;
import org.springframework.ide.eclipse.webflow.core.model.IViewState;
import org.springframework.ide.eclipse.webflow.core.model.IWebflowModelElement;
import org.springframework.ide.eclipse.webflow.core.model.IWebflowState;
import org.springframework.ide.eclipse.webflow.ui.graph.model.WebflowModelLabelDecorator;
import org.springframework.ide.eclipse.webflow.ui.graph.model.WebflowModelLabelProvider;
import org.springframework.ide.eclipse.webflow.ui.graph.policies.StateEditPolicy;
import org.springframework.ide.eclipse.webflow.ui.graph.policies.StateTreeContainerEditPolicy;
import org.springframework.ide.eclipse.webflow.ui.graph.policies.StateTreeEditPolicy;

/**
 * 
 */
public class StateTreeEditPart extends
		AbstractTreeEditPart implements
		PropertyChangeListener {

	/**
	 * 
	 */
	protected static ILabelProvider labelProvider = new DecoratingLabelProvider(
			new WebflowModelLabelProvider(), new WebflowModelLabelDecorator());

	/**
	 * 
	 */
	protected static WebflowModelLabelProvider tLabelProvider = new WebflowModelLabelProvider();

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#activate()
	 */
	public void activate() {
		super.activate();
		((IWebflowModelElement) getModel()).addPropertyChangeListener(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractTreeEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new StateEditPolicy());
		installEditPolicy(EditPolicy.TREE_CONTAINER_ROLE,
				new StateTreeContainerEditPolicy());
		installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE,
				new StateTreeEditPolicy());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#deactivate()
	 */
	public void deactivate() {
		((IWebflowModelElement) getModel()).removePropertyChangeListener(this);
		super.deactivate();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	protected List getModelChildren() {
		List children = new ArrayList();
		if (getModel() instanceof IAttributeEnabled) {
			IAttributeEnabled properties = (IAttributeEnabled) getModel();
			if (properties.getAttributes() != null) {
				children.addAll(properties.getAttributes());
			}
		}
		if (getModel() instanceof IState) {
			IState state = (IState) getModel();
			if (state.getEntryActions() != null) {
				children.addAll((state.getEntryActions().getEntryActions()));
			}
		}
		if (getModel() instanceof IActionState) {
			if (((IActionState) getState()).getActions() != null) {
				children.addAll(((IActionState) getState()).getActions());
			}
		}
		if (getModel() instanceof IViewState) {
			if (((IViewState) getState()).getRenderActions() != null) {
				children.addAll(((IViewState) getState()).getRenderActions()
						.getRenderActions());
			}
		}
		else if (getModel() instanceof ISubflowState) {
			if (((ISubflowState) getModel()).getAttributeMapper() != null)
				children.add(((ISubflowState) getModel()).getAttributeMapper());
		}
		else if (getModel() instanceof IInlineFlowState) {
			children.addAll(((IInlineFlowState) getModel()).getWebFlowState()
					.getInlineFlowStates());
		}
		else if (getModel() instanceof IWebflowState) {
			if (((IWebflowState) getState()).getStates() != null)
				children.addAll(((IWebflowState) getState()).getStates());
		}
		else if (getModel() instanceof IDecisionState) {
			if (((IDecisionState) getModel()).getIfs() != null) {
				children.addAll(((IDecisionState) getModel()).getIfs());
			}
		}
		if (getModel() instanceof IState) {
			IState state = (IState) getModel();
			if (state.getExitActions() != null) {
				children.addAll((state.getExitActions().getExitActions()));
			}
			if (state.getExceptionHandlers() != null) {
				children.addAll((state.getExceptionHandlers()));
			}
		}

		return children;
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	protected IState getState() {
		return (IState) getModel();
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent change) {
		if (change.getPropertyName().equals(IWebflowModelElement.ADD_CHILDREN)) {
			addChild(createChild(change.getNewValue()), ((Integer) change
					.getOldValue()).intValue());
		}
		else if (change.getPropertyName().equals(
				IWebflowModelElement.REMOVE_CHILDREN)) {
			// remove child
			removeChild((EditPart) getViewer().getEditPartRegistry().get(
					change.getNewValue()));
		}
		else if (change.getPropertyName().equals(
				IWebflowModelElement.MOVE_CHILDREN)) {
			refreshChildren();
		}
		else {
			refreshVisuals();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractTreeEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		Image image = labelProvider.getImage(getModel());
		String text = tLabelProvider.getText(getModel(), true, false, false);
		if (image != null) {
			setWidgetImage(image);
		}
		setWidgetText(text);
	}
}