package com.kmp.pyr.inner.ui.variants

import com.kmp.pyr.inner.localization.AppLanguage

private data class Phrase(
    val en: String,
    val es: String,
    val fr: String,
    val de: String,
    val it: String,
    val ko: String,
    val ja: String,
)

private val VARIANT_TWO_PHRASES = listOf(
    Phrase(
        en = "Preparing your bonus...",
        es = "Preparando tu bono...",
        fr = "Préparation de votre bonus...",
        de = "Dein Bonus wird vorbereitet...",
        it = "Preparazione del tuo bonus...",
        ko = "보너스를 준비하는 중...",
        ja = "ボーナスを準備中...",
    ),
    Phrase(
        en = "Calculating winning streaks...",
        es = "Calculando rachas ganadoras...",
        fr = "Calcul des séries gagnantes...",
        de = "Gewinnsträhnen werden berechnet...",
        it = "Calcolo delle serie vincenti...",
        ko = "연승 기록을 계산하는 중...",
        ja = "連勝記録を計算中...",
    ),
    Phrase(
        en = "Almost there...",
        es = "Ya casi está...",
        fr = "Presque terminé...",
        de = "Fast geschafft...",
        it = "Ci siamo quasi...",
        ko = "거의 다 됐어요...",
        ja = "もうすぐです...",
    ),
    Phrase(
        en = "Joined by 50,000+ players today",
        es = "Más de 50.000 jugadores hoy",
        fr = "Plus de 50 000 joueurs aujourd'hui",
        de = "Über 50.000 Spieler heute dabei",
        it = "Oltre 50.000 giocatori oggi",
        ko = "오늘 50,000명 이상의 플레이어가 함께해요",
        ja = "本日5万人以上のプレイヤーが参加",
    ),
    Phrase(
        en = "Live counter: 12,847 players online",
        es = "Contador en vivo: 12.847 jugadores conectados",
        fr = "Compteur en direct : 12 847 joueurs en ligne",
        de = "Live-Zähler: 12.847 Spieler online",
        it = "Contatore live: 12.847 giocatori online",
        ko = "실시간 카운터: 12,847명 접속 중",
        ja = "ライブカウンター：12,847人がオンライン",
    ),
    Phrase(
        en = "Shuffling the deck just for you...",
        es = "Barajando las cartas solo para ti...",
        fr = "On mélange les cartes rien que pour vous...",
        de = "Wir mischen die Karten nur für dich...",
        it = "Mescoliamo le carte solo per te...",
        ko = "당신만을 위해 카드를 섞는 중...",
        ja = "あなたのためにカードをシャッフル中...",
    ),
    Phrase(
        en = "Warming up the lucky reels...",
        es = "Calentando los rodillos de la suerte...",
        fr = "Préchauffage des rouleaux de la chance...",
        de = "Die Glücksrollen werden aufgewärmt...",
        it = "Riscaldamento dei rulli fortunati...",
        ko = "행운의 릴을 예열하는 중...",
        ja = "ラッキーリールをウォームアップ中...",
    ),
    Phrase(
        en = "Finding your perfect game...",
        es = "Buscando tu juego perfecto...",
        fr = "Recherche de votre jeu parfait...",
        de = "Dein perfektes Spiel wird gesucht...",
        it = "Ricerca del tuo gioco perfetto...",
        ko = "당신에게 딱 맞는 게임을 찾는 중...",
        ja = "あなたにぴったりのゲームを検索中...",
    ),
    Phrase(
        en = "Polishing your golden chips...",
        es = "Puliendo tus fichas doradas...",
        fr = "On polit vos jetons dorés...",
        de = "Deine goldenen Chips werden poliert...",
        it = "Lucidiamo le tue fiches dorate...",
        ko = "황금 칩을 닦는 중...",
        ja = "ゴールデンチップを磨いています...",
    ),
    Phrase(
        en = "Syncing your rewards balance...",
        es = "Sincronizando tu saldo de recompensas...",
        fr = "Synchronisation de votre solde de récompenses...",
        de = "Dein Prämienguthaben wird synchronisiert...",
        it = "Sincronizzazione del saldo premi...",
        ko = "리워드 잔액을 동기화하는 중...",
        ja = "リワード残高を同期中...",
    ),
    Phrase(
        en = "Unlocking exclusive offers...",
        es = "Desbloqueando ofertas exclusivas...",
        fr = "Déverrouillage des offres exclusives...",
        de = "Exklusive Angebote werden freigeschaltet...",
        it = "Sblocco delle offerte esclusive...",
        ko = "독점 혜택을 잠금 해제하는 중...",
        ja = "限定オファーを解除中...",
    ),
    Phrase(
        en = "Spinning up something special...",
        es = "Preparando algo especial...",
        fr = "On prépare quelque chose de spécial...",
        de = "Etwas Besonderes wird vorbereitet...",
        it = "Stiamo preparando qualcosa di speciale...",
        ko = "특별한 무언가를 준비하는 중...",
        ja = "特別な何かを準備中...",
    ),
    Phrase(
        en = "Recent win: Mike cashed out 2x!",
        es = "Ganancia reciente: ¡Mike retiró el doble!",
        fr = "Gain récent : Mike a doublé sa mise !",
        de = "Aktueller Gewinn: Mike hat verdoppelt!",
        it = "Vincita recente: Mike ha raddoppiato!",
        ko = "최근 당첨: Mike님이 2배로 출금했어요!",
        ja = "最近の勝利：Mikeさんが2倍で出金！",
    ),
    Phrase(
        en = "Daily streak bonus on the way...",
        es = "Bono de racha diaria en camino...",
        fr = "Bonus de série quotidienne en route...",
        de = "Täglicher Streak-Bonus ist unterwegs...",
        it = "Bonus serie giornaliera in arrivo...",
        ko = "일일 연속 보너스가 곧 도착해요...",
        ja = "デイリー連続ボーナスがまもなく到着...",
    ),
    Phrase(
        en = "Checking the leaderboard...",
        es = "Consultando la clasificación...",
        fr = "Vérification du classement...",
        de = "Bestenliste wird geprüft...",
        it = "Controllo della classifica...",
        ko = "리더보드를 확인하는 중...",
        ja = "ランキングを確認中...",
    ),
    Phrase(
        en = "Trusted by players in 30+ countries",
        es = "Con la confianza de jugadores en más de 30 países",
        fr = "La confiance des joueurs dans plus de 30 pays",
        de = "Vertraut von Spielern in über 30 Ländern",
        it = "Scelto dai giocatori in oltre 30 paesi",
        ko = "30개국 이상의 플레이어가 신뢰해요",
        ja = "30か国以上のプレイヤーに信頼されています",
    ),
    Phrase(
        en = "Securing your session...",
        es = "Asegurando tu sesión...",
        fr = "Sécurisation de votre session...",
        de = "Deine Sitzung wird gesichert...",
        it = "Protezione della tua sessione...",
        ko = "세션을 보호하는 중...",
        ja = "セッションを保護中...",
    ),
    Phrase(
        en = "Get ready — fun is loading!",
        es = "Prepárate: ¡la diversión está cargando!",
        fr = "Préparez-vous : le fun arrive !",
        de = "Mach dich bereit – der Spaß lädt!",
        it = "Preparati: il divertimento sta caricando!",
        ko = "준비하세요 — 즐거움이 로딩 중!",
        ja = "準備してください — 楽しさをロード中！",
    ),
)

internal fun variantTwoPhrases(language: AppLanguage): List<String> =
    VARIANT_TWO_PHRASES.map { phrase ->
        when (language) {
            AppLanguage.EN -> phrase.en
            AppLanguage.ES -> phrase.es
            AppLanguage.FR -> phrase.fr
            AppLanguage.DE -> phrase.de
            AppLanguage.IT -> phrase.it
            AppLanguage.KO -> phrase.ko
            AppLanguage.JA -> phrase.ja
        }
    }
