# Right Click Replant

Mod Fabric **client-side only** para Minecraft **26.3** que colhe e replanta automaticamente ao clicar com o botão direito em plantações maduras.

## Funcionalidades

- Clica com o botão direito numa plantação madura (Trigo, Cenoura, Batata, Beterraba ou Nether Wart) para a colher e replantar automaticamente.
- Usa a semente correspondente que tiver na hotbar (prefere o slot selecionado).
- Se estiver agachado (`Shift`), o comportamento normal do jogo é mantido.
- Só funciona no lado do cliente (não precisa estar no servidor).

## Dependências

- [Fabric Loader](https://fabricmc.net/use/) >= 0.19.5
- [Fabric API](https://modrinth.com/mod/fabric-api) 0.160.5+26.3 (ou compatível para 26.3)
- Minecraft ~26.3
- Java 25

## Build

```bash
./gradlew build
```

O `.jar` gerado fica em `build/libs/` (usa o jar sem `-sources`).

## Instalação

1. Instala Fabric Loader para 26.3.
2. Coloca `fabric-api` e este mod na pasta `mods/`.
3. Entra no jogo.

## Notas

- Sem semente na hotbar, colhe mas não replanta.
- Em servidores com anti-cheat forte, partir e plantar no mesmo tick pode ser sinalizado. Se necessário, pode ser adicionado um pequeno delay.
- Compatível com nomes não obfuscados do 26.x (Mojang mappings).

## Licença

MIT
