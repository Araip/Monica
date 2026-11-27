package takagi.ru.monica.ui.screens

import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import takagi.ru.monica.ui.components.SettingsPanelGroup
import takagi.ru.monica.ui.components.SettingsPanelRow
import takagi.ru.monica.R

/**
 * 同步与备份页面 - 整合导入导出和云同步功能
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SyncBackupScreen(
    onNavigateBack: () -> Unit,
    onNavigateToExportData: () -> Unit = {},
    onNavigateToImportData: () -> Unit = {},
    onNavigateToWebDav: () -> Unit = {},
    onNavigateToLocalKeePass: () -> Unit = {},  // 本地 KeePass 数据库管理
    isPlusActivated: Boolean = false
) {
    val scrollState = rememberScrollState()

    // 准备共享元素 Modifier
    val sharedTransitionScope = takagi.ru.monica.ui.LocalSharedTransitionScope.current
    val animatedVisibilityScope = takagi.ru.monica.ui.LocalAnimatedVisibilityScope.current
    
    var sharedModifier: Modifier = Modifier
    if (false && sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope!!) {
            sharedModifier = Modifier.sharedBounds(
                sharedContentState = rememberSharedContentState(key = "sync_settings_card"),
                animatedVisibilityScope = animatedVisibilityScope!!,
                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
            )
        }
    }
    
    Scaffold(
        modifier = sharedModifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.sync_backup_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.go_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            SyncBackupSection(title = stringResource(R.string.sync_backup_database_tools)) {
                SyncBackupItem(Icons.Default.Key, Setting.LOCAL_KEEPASS_DATABASE,
                    Setting.LOCAL_KEEPASS_DATABASE.summary(), onNavigateToLocalKeePass)
            }
            SyncBackupSection(title = stringResource(R.string.sync_backup_common_sync)) {
                // 附近设备同步/文件夹备份/MDBX/Bitwarden 已删除（富江改造）
                SyncBackupItem(Icons.Default.Cloud, Setting.WEBDAV_BACKUP,
                    Setting.WEBDAV_BACKUP.summary(), onNavigateToWebDav)
            }
            SyncBackupSection(title = stringResource(R.string.sync_backup_import_export_low_freq)) {
                SyncBackupItem(Icons.Default.Download, Setting.EXPORT_DATA,
                    Setting.EXPORT_DATA.summary(), onNavigateToExportData)
                SyncBackupItem(Icons.Default.Upload, Setting.IMPORT_DATA,
                    Setting.IMPORT_DATA.summary(), onNavigateToImportData)
            }
            // 提示卡片
            if (!isPlusActivated) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            stringResource(R.string.sync_backup_plus_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * 功能分类区块
 */
@Composable
private fun SyncBackupSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    SettingsPanelGroup(title, Modifier.padding(horizontal = 12.dp, vertical = 4.dp), content)
}

/**
 * 功能项
 */
@Composable
private fun SyncBackupItem(
    icon: ImageVector,
    setting: Setting,
    description: String = setting.summary(),
    onClick: () -> Unit,
    enabled: Boolean = true,
    badge: String? = null
) {
    val title = setting.title()
    SettingsPanelRow(icon, setting, description, onClick, enabled,
        trailing = if (badge != null) {{ Text(badge, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant) }} else null)
}


