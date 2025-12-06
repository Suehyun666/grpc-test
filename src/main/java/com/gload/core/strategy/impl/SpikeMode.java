package com.gload.core.strategy.impl;

import com.gload.core.grpc.GrpcClientPool;
import com.gload.core.generator.PayloadGenerator;
import com.gload.core.execution.TestModeContext;
import com.gload.core.strategy.AbstractBaseMode;
import com.gload.model.TestScenario;
import com.google.protobuf.Descriptors;

public class SpikeMode extends AbstractBaseMode {
    @Override
    public void execute(TestScenario scenario, Descriptors.MethodDescriptor methodDesc, GrpcClientPool clientPool, PayloadGenerator payloadGen, TestModeContext context) {

    }

    @Override
    public void stop() {

    }
}
