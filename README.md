# MK Instalações Elétricas

Aplicativo Android nativo para uso do eletricista, com armazenamento local e funcionamento offline.

## Recursos da versão inicial
- Cadastro de clientes.
- Catálogo de materiais e serviços com preços editáveis.
- Criação de orçamentos com materiais e mão de obra separados.
- Desconto e total calculados automaticamente.
- Histórico local de orçamentos.
- Geração e compartilhamento de PDF.
- Contato comercial: 54 99674-7442.

## Gerar APK
O GitHub Actions compila automaticamente a versão Debug a cada push na branch `main`.
1. Abra a aba **Actions** do repositório.
2. Entre em **Gerar APK Android** e aguarde o resultado verde.
3. Abra a execução concluída e baixe o artefato **MK-Instalacoes-Eletricas-APK**.
4. Extraia o ZIP para obter `app-debug.apk`.

O APK desta etapa é de teste (debug), não assinado para publicação na Play Store.

## Dados e privacidade
Os cadastros e orçamentos ficam em banco SQLite no próprio aparelho. Desinstalar o aplicativo pode apagar esses dados. Faça cópias dos PDFs compartilhados para guardar documentos importantes.

## Requisitos de compilação
JDK 17, Gradle 8.9 e Android Gradle Plugin 8.7.3.
