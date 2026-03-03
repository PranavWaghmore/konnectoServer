package pw.coding.data.repository.skill

import pw.coding.data.models.Skill

interface SkillRepository {

    suspend fun getSkills(): List<Skill>
}