package com.nesxha.readit

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.BoxWithConstraints
import kotlinx.coroutines.delay

import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.drop

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nesxha.readit.auth.Auth
import com.nesxha.readit.data.NumCat
import com.nesxha.readit.data.Store
import com.nesxha.readit.ui.auth.AuthGate
import com.nesxha.readit.ui.auth.ChangePinScreen
import com.nesxha.readit.ui.components.FlameCelebration
import com.nesxha.readit.ui.components.LevelUpCelebration
import com.nesxha.readit.ui.member.AccountScreen
import com.nesxha.readit.ui.member.AdminScreen
import com.nesxha.readit.ui.member.LeaderboardScreen
import com.nesxha.readit.ui.member.MemberProfileScreen
import com.nesxha.readit.ui.screens.BadgesScreen
import com.nesxha.readit.ui.screens.ConverterScreen
import com.nesxha.readit.ui.screens.DictionaryScreen
import com.nesxha.readit.ui.screens.FlashcardScreen
import com.nesxha.readit.ui.screens.HomeScreen
import com.nesxha.readit.ui.screens.LearnScreen
import com.nesxha.readit.ui.screens.NewBadgeDialog
import com.nesxha.readit.ui.screens.NumberLessonScreen
import com.nesxha.readit.ui.screens.PracticeScreen
import com.nesxha.readit.ui.screens.SettingsScreen
import com.nesxha.readit.ui.screens.StatsScreen
import com.nesxha.readit.ui.screens.WordDetailScreen
import com.nesxha.readit.ui.theme.ReadItTheme
import com.nesxha.readit.platform.PlatUi

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    Tab("home", "Beranda", Icons.Rounded.Home),
    Tab("learn", "Belajar", Icons.Rounded.Style),
    Tab("practice", "Latihan", Icons.Rounded.Quiz),
    Tab("dict", "Kamus", Icons.AutoMirrored.Rounded.MenuBook),
    Tab("profile", "Profil", Icons.Rounded.Person),
)

private fun tabIndex(route: String?): Int = tabs.indexOfFirst { it.route == route }

/** Keadaan tampilan bersama: [focusMode] menyembunyikan bilah navigasi saat sedang latihan. */
object AppChrome {
    var focusMode by mutableStateOf(false)
}

fun NavHostController.go(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

private const val DUR = 380

private fun AnimatedContentTransitionScope<NavBackStackEntry>.enter(): EnterTransition {
    val from = tabIndex(initialState.destination.route)
    val to = tabIndex(targetState.destination.route)
    return if (from >= 0 && to >= 0) {
        val dir = if (to > from) 1 else -1
        slideInHorizontally(tween(DUR, easing = FastOutSlowInEasing)) { dir * it / 4 } + fadeIn(tween(DUR))
    } else {
        slideInHorizontally(tween(DUR, easing = FastOutSlowInEasing)) { it / 3 } + fadeIn(tween(DUR)) + scaleIn(tween(DUR), initialScale = 0.96f)
    }
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.exit(): ExitTransition {
    val from = tabIndex(initialState.destination.route)
    val to = tabIndex(targetState.destination.route)
    return if (from >= 0 && to >= 0) {
        val dir = if (to > from) 1 else -1
        slideOutHorizontally(tween(DUR, easing = FastOutSlowInEasing)) { -dir * it / 4 } + fadeOut(tween(DUR / 2))
    } else {
        fadeOut(tween(DUR / 2)) + scaleOut(tween(DUR), targetScale = 0.96f)
    }
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.popEnter(): EnterTransition =
    fadeIn(tween(DUR)) + scaleIn(tween(DUR), initialScale = 0.96f)

private fun AnimatedContentTransitionScope<NavBackStackEntry>.popExit(): ExitTransition =
    slideOutHorizontally(tween(DUR, easing = FastOutSlowInEasing)) { it / 3 } + fadeOut(tween(DUR))

@Composable
private fun AppScaffold(nav: NavHostController) {
    val entry by nav.currentBackStackEntryAsState()
    val current = entry?.destination?.route
    val showBar = tabIndex(current) >= 0 && !AppChrome.focusMode
    val barOffset by animateFloatAsState(if (showBar) 0f else 1f, tween(300), label = "bar")

    BoxWithConstraints(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Layar lebar (desktop/tablet): navigasi di kiri, isi dibatasi lebarnya di tengah.
        val wide = maxWidth >= 840.dp
        Row(Modifier.fillMaxSize()) {
            if (wide && showBar) SideRail(current) { nav.go(it) }
            Column(Modifier.weight(1f).fillMaxHeight()) {
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
          Box(Modifier.widthIn(max = if (wide) 1100.dp else Dp.Infinity).fillMaxSize()) {
            NavHost(
                nav, startDestination = "home",
                enterTransition = { enter() },
                exitTransition = { exit() },
                popEnterTransition = { if (tabIndex(targetState.destination.route) >= 0 && tabIndex(initialState.destination.route) >= 0) enter() else popEnter() },
                popExitTransition = { if (tabIndex(targetState.destination.route) >= 0 && tabIndex(initialState.destination.route) >= 0) exit() else popExit() },
            ) {
                // Tab utama
                composable("home") { HomeScreen(nav) }
                composable("learn") { LearnScreen(nav) }
                composable("practice") { PracticeScreen(nav) }
                composable("dict") { DictionaryScreen(nav) }
                composable("profile") { StatsScreen(nav) }
                // Layar lain
                composable("flash") { FlashcardScreen(nav) }
                composable("settings") { SettingsScreen(nav) }
                composable("badges") { BadgesScreen(nav) }
                composable("converter") { ConverterScreen(nav) }
                composable("lesson/{cat}") { e ->
                    val cat = runCatching { NumCat.valueOf(e.arguments?.getString("cat") ?: "UMUM") }.getOrDefault(NumCat.UMUM)
                    NumberLessonScreen(nav, cat)
                }
                composable("drill/{cat}") { e ->
                    val cat = runCatching { NumCat.valueOf(e.arguments?.getString("cat") ?: "UMUM") }.getOrDefault(NumCat.UMUM)
                    PracticeScreen(nav, drillCat = cat)
                }
                composable("account") { AccountScreen(nav) }
                composable("mail") { com.nesxha.readit.ui.member.MailScreen(nav) }
                composable("card") { com.nesxha.readit.ui.member.MemberCardScreen(nav) }
                composable("arena") { com.nesxha.readit.ui.games.ArenaScreen(nav) }
                composable("kilat") { PracticeScreen(nav, startMode = com.nesxha.readit.data.PracticeMode.KILAT) }
                composable("rankrules") { com.nesxha.readit.ui.games.RankRulesScreen(nav) }
                composable("ranklist") { com.nesxha.readit.ui.games.RankListScreen(nav) }
                composable("kilathistory") { com.nesxha.readit.ui.games.KilatHistoryScreen(nav) }
                composable("leaderboard") { LeaderboardScreen(nav) }
                composable("changepin") { ChangePinScreen { nav.popBackStack() } }
                composable("admin") { AdminScreen(nav) }
                composable("member/{pid}") { MemberProfileScreen(nav, it.arguments?.getString("pid") ?: "") }
                composable("word/{id}", arguments = listOf(navArgument("id") { type = NavType.IntType })) {
                    WordDetailScreen(nav, it.arguments?.getInt("id") ?: 1)
                }
            }
          }
        }
        if (!wide && barOffset < 1f) {
            BottomBar(current, Modifier.graphicsLayer { translationY = barOffset * size.height; alpha = 1f - barOffset }) { nav.go(it) }
        }
            }
        }
    }
}

@Composable
private fun SideRail(current: String?, onSelect: (String) -> Unit) {
    val cs = MaterialTheme.colorScheme
    Column(
        Modifier.fillMaxHeight().width(220.dp).background(cs.surface).padding(horizontal = 14.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text("Kuaile Learn", style = MaterialTheme.typography.titleLarge, color = cs.primary, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
        Spacer(Modifier.height(12.dp))
        tabs.forEach { t ->
            val sel = t.route == current
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                    .background(if (sel) cs.primaryContainer else Color.Transparent)
                    .clickable { onSelect(t.route) }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(t.icon, null, tint = if (sel) cs.primary else cs.onSurfaceVariant, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(12.dp))
                Text(t.label, style = MaterialTheme.typography.labelLarge, color = if (sel) cs.primary else cs.onSurface)
            }
        }
    }
}

@Composable
private fun BottomBar(current: String?, modifier: Modifier, onSelect: (String) -> Unit) {
    Row(
        modifier.fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp), ambientColor = Color(0x331E3A8A), spotColor = Color(0x331E3A8A))
            .clip(RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
            .background(MaterialTheme.colorScheme.surface)
            .navigationBarsPadding()
            .padding(horizontal = 6.dp, vertical = 8.dp),
    ) {
        tabs.forEach { t ->
            val sel = current == t.route
            val pillW by animateDpAsState(if (sel) 58.dp else 32.dp, spring(dampingRatio = 0.6f, stiffness = 500f), label = "pill")
            val pillC by animateColorAsState(if (sel) MaterialTheme.colorScheme.primary else Color.Transparent, label = "pillc")
            val iconC by animateColorAsState(if (sel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant, label = "iconc")
            val textC by animateColorAsState(if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, label = "textc")
            val sc by animateFloatAsState(if (sel) 1.12f else 1f, spring(dampingRatio = 0.4f, stiffness = 400f), label = "sc")
            Column(
                Modifier.weight(1f).clip(RoundedCornerShape(18.dp))
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onSelect(t.route) }
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(Modifier.width(pillW).height(32.dp).clip(RoundedCornerShape(50)).background(pillC), contentAlignment = Alignment.Center) {
                    Icon(t.icon, t.label, tint = iconC, modifier = Modifier.size(22.dp).graphicsLayer { scaleX = sc; scaleY = sc })
                }
                Spacer(Modifier.height(4.dp))
                Text(t.label, style = MaterialTheme.typography.labelSmall, color = textC, maxLines = 1)
            }
        }
    }
}


/**
 * Aplikasi lengkap (dipakai Android dan web): gerbang akun, navigasi, dan perayaan.
 * [openRoute] = layar yang diminta dibuka dari luar (notifikasi/widget), lalu [onRouteConsumed] dipanggil.
 */
@Composable
fun KuaileApp(openRoute: String? = null, onRouteConsumed: () -> Unit = {}) {
    val st by Store.state.collectAsState()
    ReadItTheme(st.settings.darkMode) {
        AuthGate(onReady = {}) {
            val nav = rememberNavController()
            // Progres akun disinkronkan antara HP dan web.
            LaunchedEffect(Unit) {
                // Sinkron dulu, baru tandai hadir: kalau perangkat lain sudah hadir hari ini, XP harian tidak dobel.
                com.nesxha.readit.data.ProgressSync.now()
                Store.markOpen()
                launch { Store.changes.drop(1).collect { com.nesxha.readit.data.ProgressSync.later() } }
                while (true) { delay(60_000); com.nesxha.readit.data.ProgressSync.now() }
            }
            LaunchedEffect(openRoute) {
                if (openRoute != null) {
                    // Rute dari intent luar bisa berisi teks sembarang: hanya rute yang dikenal yang dibuka.
                    runCatching { if (tabIndex(openRoute) >= 0) nav.go(openRoute) else nav.navigate(openRoute) }
                    onRouteConsumed()
                }
            }
            val fresh by Store.newBadges.collectAsState()
            val flame by Store.flameCelebration.collectAsState()
            val levelUp by Store.levelUp.collectAsState()
            Box(Modifier.fillMaxSize()) {
                AppScaffold(nav)
                val f = flame
                val lv = levelUp
                when {
                    f != null -> FlameCelebration(f) { Store.clearFlameCelebration() }
                    lv != null -> LevelUpCelebration(lv) { Store.clearLevelUp() }
                    else -> {}
                }
            }
            if (flame == null && levelUp == null && fresh.isNotEmpty()) NewBadgeDialog(fresh) { Store.clearNewBadges() }
            PlatUi.ui.UpdateCheck()
        }
    }
}
