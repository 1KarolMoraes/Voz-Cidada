const fs = require("node:fs");
const path = require("node:path");
const { test } = require("node:test");
const { initializeTestEnvironment, assertFails, assertSucceeds } =
    require("@firebase/rules-unit-testing");
const {
    doc,
    collection,
    query,
    where,
    getDoc,
    getDocs,
    setDoc,
    deleteDoc,
    serverTimestamp
} = require("firebase/firestore");

test("Usuário não autenticado não pode ler publicações", async () => {
    const testEnv = await initializeTestEnvironment({
        projectId: "demo-voz-cidada",
        firestore: {
            host: "127.0.0.1",
            port: 8080,
            rules: fs.readFileSync(
                path.join(__dirname, "../../firestore.rules"),
                "utf8"
            )
        }
    });

    try {
        const db = testEnv.unauthenticatedContext().firestore();
        const publicacao = doc(db, "publicacoes", "publicacao-teste");

        await assertFails(getDoc(publicacao));
    } finally {
        await testEnv.cleanup();
    }
});

test("Usuário autenticado pode ler publicações", async () => {
    const testEnv = await initializeTestEnvironment({
        projectId: "demo-voz-cidada",
        firestore: {
            host: "127.0.0.1",
            port: 8080,
            rules: fs.readFileSync(
                path.join(__dirname, "../../firestore.rules"),
                "utf8"
            )
        }
    });

    try {
        const db = testEnv.authenticatedContext("usuario-teste").firestore();
        const publicacao = doc(db, "publicacoes", "publicacao-teste");

        await getDoc(publicacao);
    } finally {
        await testEnv.cleanup();
    }
});

test("Somente o autor pode consultar a autoria privada", async () => {
    const testEnv = await initializeTestEnvironment({
        projectId: "demo-voz-cidada",
        firestore: {
            host: "127.0.0.1",
            port: 8080,
            rules: fs.readFileSync(
                path.join(__dirname, "../../firestore.rules"),
                "utf8"
            )
        }
    });

    try {
        await testEnv.clearFirestore();

        await testEnv.withSecurityRulesDisabled(async (context) => {
            const dbAdmin = context.firestore();

            await setDoc(
                doc(dbAdmin, "autoriasPublicacoes", "publicacao-teste"),
                { autorId: "usuario-teste" }
            );
        });

        const dbAutor = testEnv
            .authenticatedContext("usuario-teste")
            .firestore();

        const dbOutro = testEnv
            .authenticatedContext("outro-usuario")
            .firestore();

        const autoriaAutor = doc(
            dbAutor,
            "autoriasPublicacoes",
            "publicacao-teste"
        );

        const autoriaOutro = doc(
            dbOutro,
            "autoriasPublicacoes",
            "publicacao-teste"
        );

        await assertSucceeds(getDoc(autoriaAutor));
        await assertFails(getDoc(autoriaOutro));

    } finally {
        await testEnv.cleanup();
    }
});


test("Usuário autenticado pode curtir uma publicação", async () => {
    const testEnv = await initializeTestEnvironment({
        projectId: "demo-voz-cidada",
        firestore: {
            host: "127.0.0.1",
            port: 8080,
            rules: fs.readFileSync(
                path.join(__dirname, "../../firestore.rules"),
                "utf8"
            )
        }
    });

    try {
        await testEnv.clearFirestore();

        const db = testEnv.authenticatedContext("usuario-teste").firestore();
        await testEnv.withSecurityRulesDisabled(async (context) => {
            const dbAdmin = context.firestore();

            await setDoc(doc(dbAdmin, "publicacoes", "publicacao-teste"), {
                categoria: "Infraestrutura",
                localizacao: "Manaus",
                descricao: "Publicação fictícia para teste",
                anonimo: true,
                criadoEm: new Date(),
                likesCount: 0,
                commentsCount: 0
            });
        });
        const publicacaoTeste = await getDoc(
            doc(db, "publicacoes", "publicacao-teste")
        );

        console.log(
            "Publicação existe antes da curtida?",
            publicacaoTeste.exists()
        );
        const curtida = doc(
            db,
            "publicacoes",
            "publicacao-teste",
            "likes",
            "usuario-teste"
        );

        await setDoc(curtida, {
            createdAt: serverTimestamp()
        });
    } finally {
        await testEnv.cleanup();
    }
});
test("Usuário não pode curtir em nome de outra pessoa", async () => {
    const testEnv = await initializeTestEnvironment({
        projectId: "demo-voz-cidada",
        firestore: {
            host: "127.0.0.1",
            port: 8080,
            rules: fs.readFileSync(
                path.join(__dirname, "../../firestore.rules"),
                "utf8"
            )
        }
    });

    try {
        await testEnv.clearFirestore();

        await testEnv.withSecurityRulesDisabled(async (context) => {
            const dbAdmin = context.firestore();

            await setDoc(
                doc(dbAdmin, "publicacoes", "publicacao-teste"),
                {
                    categoria: "Infraestrutura",
                    localizacao: "Manaus",
                    descricao: "Publicação fictícia",
                    anonimo: true,
                    criadoEm: new Date(),
                    likesCount: 0,
                    commentsCount: 0
                }
            );
        });

        const db = testEnv.authenticatedContext("usuario-teste").firestore();

        const curtidaDeOutraPessoa = doc(
            db,
            "publicacoes",
            "publicacao-teste",
            "likes",
            "outro-usuario"
        );

        await assertFails(
            setDoc(curtidaDeOutraPessoa, {
                createdAt: serverTimestamp()
            })
        );
    } finally {
        await testEnv.cleanup();
    }
});

test("Usuário não pode curtir publicação inexistente", async () => {
    const testEnv = await initializeTestEnvironment({
        projectId: "demo-voz-cidada",
        firestore: {
            host: "127.0.0.1",
            port: 8080,
            rules: fs.readFileSync(
                path.join(__dirname, "../../firestore.rules"),
                "utf8"
            )
        }
    });

    try {
        await testEnv.clearFirestore();

        const db = testEnv.authenticatedContext("usuario-teste").firestore();

        const curtida = doc(
            db,
            "publicacoes",
            "publicacao-inexistente",
            "likes",
            "usuario-teste"
        );

        await assertFails(
            setDoc(curtida, {
                createdAt: serverTimestamp()
            })
        );
    } finally {
        await testEnv.cleanup();
    }
});

test("Usuário não pode excluir curtida de outra pessoa", async () => {
    const testEnv = await initializeTestEnvironment({
        projectId: "demo-voz-cidada",
        firestore: {
            host: "127.0.0.1",
            port: 8080,
            rules: fs.readFileSync(
                path.join(__dirname, "../../firestore.rules"),
                "utf8"
            )
        }
    });

    try {
        await testEnv.clearFirestore();

        await testEnv.withSecurityRulesDisabled(async (context) => {
            const dbAdmin = context.firestore();

            await setDoc(
                doc(dbAdmin, "publicacoes", "publicacao-teste"),
                {
                    categoria: "Infraestrutura",
                    localizacao: "Manaus",
                    descricao: "Publicação fictícia",
                    anonimo: true,
                    criadoEm: new Date(),
                    likesCount: 0,
                    commentsCount: 0
                }
            );

            await setDoc(
                doc(dbAdmin, "publicacoes", "publicacao-teste", "likes", "outro-usuario"),
                { createdAt: new Date() }
            );
        });

        const db = testEnv.authenticatedContext("usuario-teste").firestore();

        const curtidaDeOutraPessoa = doc(
            db,
            "publicacoes",
            "publicacao-teste",
            "likes",
            "outro-usuario"
        );

        await assertFails(deleteDoc(curtidaDeOutraPessoa));
    } finally {
        await testEnv.cleanup();
    }
});

test("Usuário pode excluir a própria curtida", async () => {
    const testEnv = await initializeTestEnvironment({
        projectId: "demo-voz-cidada",
        firestore: {
            host: "127.0.0.1",
            port: 8080,
            rules: fs.readFileSync(
                path.join(__dirname, "../../firestore.rules"),
                "utf8"
            )
        }
    });

    try {
        await testEnv.clearFirestore();

        await testEnv.withSecurityRulesDisabled(async (context) => {
            const dbAdmin = context.firestore();

            await setDoc(
                doc(dbAdmin, "publicacoes", "publicacao-teste"),
                {
                    categoria: "Infraestrutura",
                    localizacao: "Manaus",
                    descricao: "Publicação fictícia",
                    anonimo: true,
                    criadoEm: new Date(),
                    likesCount: 0,
                    commentsCount: 0
                }
            );

            await setDoc(
                doc(dbAdmin, "publicacoes", "publicacao-teste", "likes", "usuario-teste"),
                { createdAt: new Date() }
            );
        });

        const db = testEnv.authenticatedContext("usuario-teste").firestore();

        const minhaCurtida = doc(
            db,
            "publicacoes",
            "publicacao-teste",
            "likes",
            "usuario-teste"
        );

        await deleteDoc(minhaCurtida);
    } finally {
        await testEnv.cleanup();
    }
});

test("Usuário pode listar somente suas próprias autorias", async () => {
    const testEnv = await initializeTestEnvironment({
        projectId: "demo-voz-cidada",
        firestore: {
            host: "127.0.0.1",
            port: 8080,
            rules: fs.readFileSync(
                path.join(__dirname, "../../firestore.rules"),
                "utf8"
            )
        }
    });

    try {
        await testEnv.clearFirestore();

        await testEnv.withSecurityRulesDisabled(async (context) => {
            const dbAdmin = context.firestore();

            await setDoc(
                doc(dbAdmin, "autoriasPublicacoes", "publicacao-1"),
                { autorId: "usuario-teste" }
            );

            await setDoc(
                doc(dbAdmin, "autoriasPublicacoes", "publicacao-2"),
                { autorId: "usuario-teste" }
            );

            await setDoc(
                doc(dbAdmin, "autoriasPublicacoes", "publicacao-3"),
                { autorId: "outro-usuario" }
            );
        });

        const db = testEnv
            .authenticatedContext("usuario-teste")
            .firestore();

        // Consulta somente as autorias do usuário autenticado
        const minhasAutorias = query(
            collection(db, "autoriasPublicacoes"),
            where("autorId", "==", "usuario-teste")
        );

        const resultado = await assertSucceeds(getDocs(minhasAutorias));

        // Deve encontrar exatamente duas publicações
        if (resultado.size !== 2) {
            throw new Error(
                `Esperadas 2 publicações, encontradas ${resultado.size}`
            );
        }

        // Consulta sem filtro não deve ser permitida
        await assertFails(
            getDocs(collection(db, "autoriasPublicacoes"))
        );

        // Não pode consultar as autorias de outro usuário
        const autoriasDeOutro = query(
            collection(db, "autoriasPublicacoes"),
            where("autorId", "==", "outro-usuario")
        );

        await assertFails(getDocs(autoriasDeOutro));

    } finally {
        await testEnv.cleanup();
    }
});