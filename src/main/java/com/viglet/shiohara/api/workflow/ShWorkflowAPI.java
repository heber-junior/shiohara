/*
 * Copyright (C) 2016-2019 Alexandre Oliveira <alexandre.oliveira@viglet.com> 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.viglet.shiohara.api.workflow;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viglet.shiohara.persistence.model.auth.ShGroup;
import com.viglet.shiohara.persistence.model.auth.ShUser;
import com.viglet.shiohara.persistence.model.workflow.ShWorkflowTask;
import com.viglet.shiohara.persistence.repository.auth.ShGroupRepository;
import com.viglet.shiohara.persistence.repository.auth.ShUserRepository;
import com.viglet.shiohara.persistence.repository.post.ShPostRepository;
import com.viglet.shiohara.persistence.repository.workflow.ShWorkflowTaskRepository;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/api/v2/workflow")
@Api(tags = "Workflow", description = "Workflow API")
public class ShWorkflowAPI {

	@Autowired
	private ShWorkflowTaskRepository shWorkflowTaskRepository;
	@Autowired
	private ShUserRepository shUserRepository;
	@Autowired
	private ShGroupRepository shGroupRepository;
	@Autowired
	private ShPostRepository shPostRepository;

	@GetMapping("/task")
	public Set<ShWorkflowTask> shWorkflowTasksGet(Principal principal) {
		ShUser shUser = shUserRepository.findByUsername(principal.getName());
		List<ShUser> shUsers = new ArrayList<>();
		shUsers.add(shUser);
		Set<ShGroup> shGroups = shGroupRepository.findByShUsersIn(shUsers);
		List<String> requesters = new ArrayList<>();
		
		for (ShGroup shGroup : shGroups) {
			requesters.add(shGroup.getName());
		}
		
		Set<ShWorkflowTask> shWorkflowTasks = shWorkflowTaskRepository.findByRequestedIn(requesters);

		for (ShWorkflowTask shWorkflowTask : shWorkflowTasks) {
			String id = shWorkflowTask.getShObject().getId();
			shWorkflowTask.setShObject(shPostRepository.findByIdFull(id).get());
		}
		return shWorkflowTasks;
	}
}
