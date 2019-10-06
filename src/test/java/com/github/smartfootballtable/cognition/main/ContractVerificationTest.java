package com.github.smartfootballtable.cognition.main;

import static com.github.smartfootballtable.cognition.data.unit.DistanceUnit.CENTIMETER;
import static java.lang.Integer.MAX_VALUE;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.smartfootballtable.cognition.SFTCognition;
import com.github.smartfootballtable.cognition.data.Message;
import com.github.smartfootballtable.cognition.data.Table;

import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.loader.PactFolder;
import au.com.dius.pact.provider.junit.target.TestTarget;
import au.com.dius.pact.provider.junit5.AmpqTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;

@Provider("cognition")
@PactFolder("pacts")
public class ContractVerificationTest {

	@TestTarget
	public final AmpqTestTarget target = new AmpqTestTarget(emptyList());

	private final List<Message> sendMessages = new ArrayList<>();
	private final SFTCognition cognition = new SFTCognition(anyTable(), sendMessages::add);

	private Table anyTable() {
		return new Table(120, 68, CENTIMETER);
	}

	@BeforeEach
	void before(PactVerificationContext context) {
		// TODO verify metadata team/scored
		context.setTarget(target);
	}

	@TestTemplate
	@ExtendWith(PactVerificationInvocationContextProvider.class)
	void pactVerificationTestTemplate(PactVerificationContext context) {
		context.verifyInteraction();
	}

	@PactVerifyProvider("id of team that scored")
	public String idOfTeamThatScored() {
		cognition.messages().teamScored(MAX_VALUE);
		return payloads();
	}

	private String payloads() {
		return sendMessages.stream().map(this::toJson).collect(joining());
	}

	private String toJson(Message message) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("topic", message.getTopic());
		jsonObject.put("payload", message.getPayload());
		return jsonObject.toString();
	}

}