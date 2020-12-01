package com.myorg;

import java.util.ArrayList;
import java.util.List;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Duration;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;

import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.elasticloadbalancing.LoadBalancer;
import software.amazon.awscdk.services.elasticloadbalancing.LoadBalancerListener;
import software.amazon.awscdk.services.elasticloadbalancing.CfnLoadBalancer;
//import software.amazon.awscdk.services.elasticloadbalancing.PoliciesProperty;  // cannot find symbol!!!

public class CdkClbJavaStack extends Stack {
    public CdkClbJavaStack(final Construct parent, final String id) {
        this(parent, id, null);
    }

    public CdkClbJavaStack(final Construct parent, final String id, final StackProps props) {
        super(parent, id, props);

        Vpc vpc = Vpc.Builder.create(this, "newdevvpc").cidr("10.0.0.0/16").build();
				
		LoadBalancer loadBalancer = LoadBalancer.Builder.create(this,"LB")
                .vpc(vpc)
                .build();
        /*        
	    List<Object> attributes = new ArrayList<>();
        attributes.add("ELBSecurityPolicy-TLS-1-2-2017-01");
        
        PoliciesProperty.builder()
            .policyName("My-SSLNegotiation-Policy")
            .policyType("SSLNegotiationPolicyType")
            .attributes(attributes)
            .build();
        */
        
        List<String> policyName = new ArrayList<>();
        policyName.add("My-SSLNegotiation-Policy");
        
        
        // listener var might be not necessary
		loadBalancer.addListener(LoadBalancerListener.builder()
		                                  .externalPort(443)
		                                  .policyNames(policyName)
		                                  .build());
		
		// override by low level construct
		CfnLoadBalancer cfnLb = (CfnLoadBalancer)loadBalancer.getNode().getDefaultChild();
		cfnLb.addOverride("Properties.Policies.PolicyName", "My-SSLNegotiation-Policy");
		cfnLb.addOverride("Properties.Policies.PolicyType", "SSLNegotiationPolicyType");
		cfnLb.addOverride("Properties.Policies.Attributes.Name", "Reference-Security-Policy");
		cfnLb.addOverride("Properties.Policies.Attributes.Value", "ELBSecurityPolicy-TLS-1-2-2017-01");
    }
}
