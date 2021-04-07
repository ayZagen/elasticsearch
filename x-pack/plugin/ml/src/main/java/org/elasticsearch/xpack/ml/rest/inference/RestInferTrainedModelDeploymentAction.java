/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.ml.rest.inference;

import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.action.RestToXContentListener;
import org.elasticsearch.xpack.core.ml.action.InferTrainedModelDeploymentAction;
import org.elasticsearch.xpack.core.ml.inference.TrainedModelConfig;
import org.elasticsearch.xpack.core.ml.utils.ExceptionsHelper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.elasticsearch.rest.RestRequest.Method.POST;
import static org.elasticsearch.xpack.ml.MachineLearning.BASE_PATH;

public class RestInferTrainedModelDeploymentAction extends BaseRestHandler {

    @Override
    public String getName() {
        return "xpack_ml_infer_trained_models_deployment_action";
    }

    @Override
    public List<Route> routes() {
        return Collections.singletonList(
            new Route(
                POST,
                BASE_PATH + "trained_models/deployment/{" + TrainedModelConfig.MODEL_ID.getPreferredName() + "}/_infer")
        );
    }

    @Override
    protected RestChannelConsumer prepareRequest(RestRequest restRequest, NodeClient client) throws IOException {
        String deploymentId = restRequest.param(TrainedModelConfig.MODEL_ID.getPreferredName());
        InferTrainedModelDeploymentAction.Request request;
        if (restRequest.hasContentOrSourceParam()) {
            XContentParser parser = restRequest.contentOrSourceParamParser();
            request = InferTrainedModelDeploymentAction.Request.parseRequest(deploymentId, parser);
        } else {
            throw ExceptionsHelper.badRequestException("requires body");
        }

        return channel -> client.execute(InferTrainedModelDeploymentAction.INSTANCE, request, new RestToXContentListener<>(channel));
    }
}
