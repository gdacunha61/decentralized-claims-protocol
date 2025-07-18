/*
 *  Copyright (c) 2024 Metaform Systems, Inc.
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Metaform Systems, Inc. - initial API and implementation
 *
 */

package org.eclipse.dcp.schema.issuance;

import org.eclipse.dcp.schema.fixtures.AbstractSchemaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.networknt.schema.InputFormat.JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.dcp.schema.issuance.CredentialObjectSchemaTest.CREDENTIAL_OBJECT;

public class IssuerMetadataSchemaTest extends AbstractSchemaTest {

    public static final String ISSUER_METADATA = """
            {
                "@context": ["https://w3id.org/dspace-dcp/v1.0/dcp.jsonld"],
                "type": "IssuerMetadata",
                "issuer": "did:web:issuer-url",
                "credentialsSupported": [%s]
            }""";
    public static final String CREDENTIAL_OBJECT_INCOMPLETE = """
            {
                "id": "d5c77b0e-7f4e-4fd5-8c5f-28b5fc3f96d1",
                "type": "CredentialObject",
                "credentialType": "VerifiableCredential",
                "offerReason": "reissue",
                "bindingMethods": [
                  "did:web"
                ]
            }
            """;
    private static final String INVALID_ISSUER_METADATA = """
            {
              "@context": ["https://w3id.org/dspace-dcp/v1.0/dcp.jsonld"],
              "type": "IssuerMetadata"
            }""";
    private static final String INVALID_CREDENTIAL_REQUEST_MESSAGE_NO_TYPE_AND_CONTEXT = """
            {
              "issuer": "did:web:issuer-url",
              "credentialsSupported": [%s]
            }""";

    @Test
    void verifySchema() {
        assertThat(schema.validate(ISSUER_METADATA.formatted(CREDENTIAL_OBJECT), JSON)).isEmpty();
    }

    @Test
    void verifySchema_missingIssuerAndCredentialsSupported() {
        assertThat(schema.validate(INVALID_ISSUER_METADATA, JSON))
                .extracting(this::errorExtractor)
                .containsExactly(error("issuer", REQUIRED), error("credentialsSupported", REQUIRED));

    }

    @Test
    void verifySchema_credentialSupportedIsIncomplete() {
        assertThat(schema.validate(ISSUER_METADATA.formatted(CREDENTIAL_OBJECT_INCOMPLETE), JSON))
                .extracting(this::errorExtractor)
                .containsExactlyInAnyOrder(error("credentialSchema", REQUIRED), error("profile", REQUIRED));
    }

    @Test
    void verifySchema_missingTypeAndContext() {
        assertThat(schema.validate(INVALID_CREDENTIAL_REQUEST_MESSAGE_NO_TYPE_AND_CONTEXT.formatted(CREDENTIAL_OBJECT), JSON))
                .hasSize(2)
                .extracting(this::errorExtractor)
                .contains(error("type", REQUIRED), error("@context", REQUIRED));
    }

    @BeforeEach
    void setUp() {
        setUp("/issuance/issuer-metadata-schema.json");
    }


}
