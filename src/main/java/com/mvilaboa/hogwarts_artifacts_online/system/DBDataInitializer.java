package com.mvilaboa.hogwarts_artifacts_online.system;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.mvilaboa.hogwarts_artifacts_online.artifact.Artifact;
import com.mvilaboa.hogwarts_artifacts_online.artifact.ArtifactRepository;
import com.mvilaboa.hogwarts_artifacts_online.hogwartsuser.HogwartsUser;
import com.mvilaboa.hogwarts_artifacts_online.hogwartsuser.UserRepository;
import com.mvilaboa.hogwarts_artifacts_online.wizard.WizardRepository;
import com.mvilaboa.hogwarts_artifacts_online.wizard.Wizard;

@Component
public class DBDataInitializer implements CommandLineRunner {

    private final ArtifactRepository artifactRepository;

    private final WizardRepository wizardRepository;

    private final UserRepository userRepository;

    public DBDataInitializer(ArtifactRepository artifactRepository, WizardRepository wizardRepository,
            UserRepository userRepository) {
        this.artifactRepository = artifactRepository;
        this.wizardRepository = wizardRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        Artifact a1 = new Artifact();
        a1.setId("100001");
        a1.setName("Time Turner");
        a1.setDescription("A magical device that allows the user to travel back in time for a few hours.");
        a1.setImageUrl("ImageUrl1");

        Artifact a2 = new Artifact();
        a2.setId("100002");
        a2.setName("Pensieve");
        a2.setDescription("A shallow stone basin used to review and relive memories.");
        a2.setImageUrl("ImageUrl2");

        Artifact a3 = new Artifact();
        a3.setId("100003");
        a3.setName("Marauder's Map");
        a3.setDescription("A magical map that shows the real-time location of people within Hogwarts.");
        a3.setImageUrl("ImageUrl3");

        Artifact a4 = new Artifact();
        a4.setId("100004");
        a4.setName("Sorting Hat");
        a4.setDescription("An enchanted hat that sorts Hogwarts students into their respective houses.");
        a4.setImageUrl("ImageUrl4");

        Artifact a5 = new Artifact();
        a5.setId("100005");
        a5.setName("Philosopher's Stone");
        a5.setDescription("A legendary stone capable of producing the Elixir of Life and turning any metal into gold.");
        a5.setImageUrl("ImageUrl5");

        Artifact a6 = new Artifact();
        a6.setId("100006");
        a6.setName("Mirror of Erised");
        a6.setDescription("A mystical mirror that reveals the deepest desires of whoever gazes into it.");
        a6.setImageUrl("ImageUrl6");

        Wizard w1 = new Wizard();
        w1.setName("Albus Dumbledore");
        w1.addArtifact(a1);
        w1.addArtifact(a3);

        Wizard w2 = new Wizard();
        w2.setName("Harry Potter");
        w2.addArtifact(a2);
        w2.addArtifact(a4);

        Wizard w3 = new Wizard();
        w3.setName("Neville Longbottom");
        w3.addArtifact(a5);

        wizardRepository.save(w1);
        wizardRepository.save(w2);
        wizardRepository.save(w3);

        artifactRepository.save(a6);

        HogwartsUser u1 = new HogwartsUser();
        u1.setUsername("Eric");
        u1.setPassword("654321");
        u1.setEnabled(true);
        u1.setRoles("admin user");

        HogwartsUser u2 = new HogwartsUser();
        u2.setUsername("john");
        u2.setPassword("123456");
        u2.setEnabled(true);
        u2.setRoles("user");

        HogwartsUser u3 = new HogwartsUser();
        u3.setUsername("tom");
        u3.setPassword("qwerty");
        u3.setEnabled(false);
        u3.setRoles("user");

        userRepository.save(u1);
        userRepository.save(u2);
        userRepository.save(u3);

    }

}
